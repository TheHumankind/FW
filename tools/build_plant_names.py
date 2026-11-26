"""Builds app/src/main/assets/plant_names.json: localized names of the bundled plant catalog.

The catalog (assets/plant_catalog.json) is English. This script writes names in Russian, Ukrainian, Polish and
Belarusian for the houseplant entries. Format of the output: {"<id>": {"ru": "Main name|alias|alias", ...}};
the part before the first "|" is shown, the aliases are only searched ("щучий хвост", "money tree", ...).

Names are written by hand from general plant knowledge, not copied from a source, and are NOT reviewed by a
native speaker: treat the Polish and Belarusian ones in particular as a first pass.

Run from the repository root:  python -I tools/build_plant_names.py
"""
import json
from pathlib import Path

LANGS = ("ru", "uk", "pl", "be")
SMALL = {"of", "the", "la", "von", "de", "and"}

# Genus words: a catalog id "<genus>-<rest>" becomes "<genus in the language> <Rest>" (cultivar names stay Latin).
GENUS = {
    "aglaonema": ("Аглаонема", "Аглаонема", "Aglaonema", "Аглаанема"),
    "alocasia": ("Алоказия", "Алоказія", "Alokazja", "Алаказія"),
    "anthurium": ("Антуриум", "Антуріум", "Anturium", "Антурыум"),
    "aralia": ("Аралия", "Аралія", "Aralia", "Аралія"),
    "begonia": ("Бегония", "Бегонія", "Begonia", "Бегонія"),
    "calathea": ("Калатея", "Калатея", "Kalatea", "Калатэя"),
    "croton": ("Кротон", "Кротон", "Kroton", "Кратон"),
    "dracaena": ("Драцена", "Драцена", "Dracena", "Драцэна"),
    "ficus": ("Фикус", "Фікус", "Fikus", "Фікус"),
    "hoya": ("Хойя", "Хоя", "Hoja", "Хоя"),
    "monstera": ("Монстера", "Монстера", "Monstera", "Манстэра"),
    "peperomia": ("Пеперомия", "Пеперомія", "Peperomia", "Пеперомія"),
    "philodendron": ("Филодендрон", "Філодендрон", "Filodendron", "Філадэндран"),
    "pilea": ("Пилея", "Пілея", "Pilea", "Пілея"),
    "pothos": ("Потос", "Потос", "Potos", "Потас"),
    "syngonium": ("Сингониум", "Сингоніум", "Syngonium", "Сінганіум"),
    "tradescantia": ("Традесканция", "Традесканція", "Trzykrotka", "Традэсканцыя"),
    "dieffenbachia": ("Диффенбахия", "Діффенбахія", "Difenbachia", "Дыфенбахія"),
    "alocasias": None,
}


LOOKALIKES = str.maketrans("iaceopxy", "іасеорху")


def fix_lookalikes(text: str) -> str:
    """Inside a word that contains Cyrillic letters, Latin look-alike letters (typos) become Cyrillic."""
    def fix(token: str) -> str:
        has_cyrillic = any("Ѐ" <= ch <= "ӿ" for ch in token)
        return token.translate(LOOKALIKES) if has_cyrillic else token
    return "".join(fix(part) for part in __import__("re").split(r"(\s+|\|)", text))


def title(rest: str) -> str:
    words = rest.split("-")
    return " ".join(w if w in SMALL else w.capitalize() for w in words)


# Everything that is not "<genus>-<rest>" or that deserves a proper name. Order: ru, uk, pl, be.
EXPLICIT = {
    "aglaonema": ("Аглаонема|китайское вечнозелёное", "Аглаонема", "Aglaonema", "Аглаанема"),
    "aralia-ming": ("Аралия Минг", "Аралія Мінг", "Aralia Ming", "Аралія Мінг"),
    "aspidistra-variegated": ("Аспидистра пёстролистная", "Аспідістра строката", "Aspidistra pstra", "Аспідыстра стракатая"),
    "cast-iron-plant": ("Аспидистра|чугунное растение", "Аспідістра", "Aspidistra|aspidistra wyniosła", "Аспідыстра"),
    "dracaena-fragrans": ("Драцена душистая|Драцена фрагранс", "Драцена запашна", "Dracena wonna", "Драцэна духмяная"),
    "dracaena-marginata": ("Драцена окаймлённая|Драцена маргината", "Драцена облямована", "Dracena obrzeżona", "Драцэна аблямаваная"),
    "dracaena-song-of-india": ("Драцена Song of India", "Драцена Song of India", "Dracena Song of India", "Драцэна Song of India"),
    "false-aralia": ("Ложная аралия|Схефлера изящная", "Хибна аралія", "Aralia fałszywa", "Хлусная аралія"),
    "peace-lily-sensation": ("Спатифиллум Sensation|женское счастье", "Спатіфілум Sensation", "Skrzydłokwiat Sensation", "Спатыфілум Sensation"),
    "pilea-glauca": ("Пилея сизая|Silver Sparkle", "Пілея сиза", "Pilea sina", "Пілея сіва"),
    "pothos": ("Эпипремнум|Потос|Сциндапсус золотистый", "Епіпремнум|Потос", "Epipremnum|Potos", "Эпіпрэмнум|Потас"),
    "golden-pothos": ("Эпипремнум золотистый|Золотой потос|Потос", "Епіпремнум золотистий|Золотий потос", "Epipremnum złociste|Złoty potos", "Эпіпрэмнум залацісты|Залаты потас"),
    "marble-queen-pothos": ("Эпипремнум Marble Queen|Потос Marble Queen", "Епіпремнум Marble Queen", "Epipremnum Marble Queen", "Эпіпрэмнум Marble Queen"),
    "neon-pothos": ("Эпипремнум Neon|Потос Neon", "Епіпремнум Neon", "Epipremnum Neon", "Эпіпрэмнум Neon"),
    "epipremnum-pinnatum-albo": ("Эпипремнум перистый Albo", "Епіпремнум перистий Albo", "Epipremnum pierzaste Albo", "Эпіпрэмнум перыстый Albo"),
    "sansevieria-cylindrica": ("Сансевиерия цилиндрическая|щучий хвост", "Сансевієрія циліндрична|щучий хвіст", "Sansewieria cylindryczna|wężownica", "Сансевіерыя цыліндрычная|шчуч хвост"),
    "sansevieria-hahnii": ("Сансевиерия Хана|Гнездо птицы", "Сансевієрія Хана", "Sansewieria Hahnii", "Сансевіерыя Хана"),
    "sansevieria-moonshine": ("Сансевиерия Moonshine", "Сансевієрія Moonshine", "Sansewieria Moonshine", "Сансевіерыя Moonshine"),
    "snake-plants": ("Сансевиерия|щучий хвост|тёщин язык|змеиное растение", "Сансевієрія|щучий хвіст|тещин язик", "Sansewieria|wężownica|język teściowej", "Сансевіерыя|шчуч хвост|цешчын язык"),
    "schefflera": ("Шеффлера|зонтичное дерево", "Шеффлера|парасолькове дерево", "Szeflera|drzewo parasolowe", "Шэфлера"),
    "schefflera-dwarf": ("Шеффлера карликовая", "Шеффлера карликова", "Szeflera karłowata", "Шэфлера карлікавая"),
    "shangri-la-pothos": ("Эпипремнум Shangri La", "Епіпремнум Shangri La", "Epipremnum Shangri La", "Эпіпрэмнум Shangri La"),
    "spathiphyllum-domino": ("Спатифиллум Domino|женское счастье", "Спатіфілум Domino", "Skrzydłokwiat Domino", "Спатыфілум Domino"),
    "peace-lilies": ("Спатифиллум|женское счастье", "Спатіфілум|жіноче щастя", "Skrzydłokwiat|skrzydłolist", "Спатыфілум|жаночае шчасце"),
    "spider-plants": ("Хлорофитум|хлорофитум хохлатый", "Хлорофітум", "Zielistka|zielistka Sternberga", "Хларафітум"),
    "yucca": ("Юкка", "Юка", "Juka|juka palmowa", "Юка"),
    "zz-plant": ("Замиокулькас|ЗЗ|долларовое дерево", "Замійокулькас|долярове дерево", "Zamiokulkas|zamiokulkas zamiolistny", "Замиякулькас"),
    "zz-plant-raven": ("Замиокулькас Raven|чёрный", "Замійокулькас Raven", "Zamiokulkas Raven", "Замиякулькас Raven"),
    "zz-plant-zenzi": ("Замиокулькас Zenzi", "Замійокулькас Zenzi", "Zamiokulkas Zenzi", "Замиякулькас Zenzi"),
    "alocasias": ("Алоказии", "Алоказії", "Alokazje", "Алаказіі"),
    "alocasia-polly": ("Алоказия Полли|Alocasia Polly|Амазонская алоказия", "Алоказія Поллі", "Alokazja Polly", "Алаказія Полі"),
    "alocasia-zebrina": ("Алоказия зебрина", "Алоказія зебрина", "Alokazja zebrina", "Алаказія зебрына"),
    "anthuriums": ("Антуриумы", "Антуріуми", "Anturium", "Антурыумы"),
    "anthurium-bird-nest": ("Антуриум Птичье гнездо", "Антуріум Пташине гніздо", "Anturium Gniazdo ptaka", "Антурыум Птушынае гняздо"),
    "anthurium-clarinervium": ("Антуриум кларинервиум", "Антуріум кларинервіум", "Anturium clarinervium", "Антурыум кларынервіум"),
    "anthurium-vittarifolium": ("Антуриум полосатолистный", "Антуріум смугастолистий", "Anturium wąskolistne", "Антурыум палосаталісты"),
    "anthurium-warocqueanum": ("Антуриум Варока|Королева", "Антуріум Вароке", "Anturium Warocque'a", "Антурыум Варока"),
    "anthurium-flamingo": ("Антуриум Фламинго|мужское счастье", "Антуріум Фламінго|чоловіче щастя", "Anturium Flamingo", "Антурыум Фламінга|мужчынскае шчасце"),
    "aroids": ("Ароидные|Ароиды", "Ароїдні|Ароїди", "Obrazkowate|aroidy", "Араідныя"),
    "arrowhead-plant": ("Сингониум ножколистный|Сингониум", "Сингоніум|Наконечник стріли", "Syngonium|Strzałka", "Сінганіум"),
    "burle-marx-philodendron": ("Филодендрон Бурле Маркс", "Філодендрон Бурле Маркс", "Filodendron Burle Marx", "Філадэндран Бурле Маркс"),
    "caladium": ("Каладиум", "Каладіум", "Kaladium|kolokazja", "Каладыум"),
    "colocasia": ("Колоказия|Таро", "Колоказія|Таро", "Kolokazja|Taro", "Калаказія|Таро"),
    "dieffenbachia": ("Диффенбахия", "Діффенбахія", "Difenbachia", "Дыфенбахія"),
    "dieffenbachia-compact": ("Диффенбахия компакта", "Діффенбахія компакта", "Difenbachia Compacta", "Дыфенбахія кампакта"),
    "monstera-albo": ("Монстера Альбо|пёстрая", "Монстера Альбо", "Monstera Albo", "Манстэра Альбо"),
    "monstera-thai-constellation": ("Монстера Тайское созвездие|Thai Constellation", "Монстера Thai Constellation", "Monstera Thai Constellation", "Манстэра Thai Constellation"),
    "monstera-adansonii": ("Монстера адансона|Монстера Адансони", "Монстера Адансона", "Monstera Adansona", "Манстэра Адансона"),
    "philodendron-heartleaf": ("Филодендрон лазающий|сердцелистный", "Філодендрон лазячий", "Filodendron pnący", "Філадэндран ліяністы"),
    "philodendron-selloum": ("Филодендрон селлоум|древовидный", "Філодендрон селлоум", "Filodendron Selloum", "Філадэндран селоўм"),
    "philodendron-brandtianum": ("Филодендрон Брандта|серебристый", "Філодендрон Брандта", "Filodendron Brandta", "Філадэндран Брандта"),
    "philodendron-silver": ("Филодендрон серебристый", "Філодендрон сріблястий", "Filodendron srebrzysty", "Філадэндран серабрысты"),
    "philodendron-white-knight": ("Филодендрон White Knight", "Філодендрон White Knight", "Filodendron White Knight", "Філадэндран White Knight"),
    "philodendron-prince-of-orange": ("Филодендрон Prince of Orange", "Філодендрон Prince of Orange", "Filodendron Prince of Orange", "Філадэндран Prince of Orange"),
    "philodendron-ring-of-fire": ("Филодендрон Ring of Fire", "Філодендрон Ring of Fire", "Filodendron Ring of Fire", "Філадэндран Ring of Fire"),
    "philodendron-brasil": ("Филодендрон Бразилия|Brasil", "Філодендрон Бразилія", "Filodendron Brasil", "Філадэндран Бразілія"),
    "rhaphidophora-tetrasperma": ("Рафидофора четырёхсемянная|Мини-монстера", "Рафідофора чотирисім'янкова|Міні-монстера", "Rafidofora|Mini monstera", "Рафідафора|Міні-манстэра"),
    "swiss-cheese-plant": ("Монстера деликатесная|Монстера|Швейцарский сыр", "Монстера ласуна|Монстера", "Monstera dziurawa|Monstera", "Манстэра|Швейцарскі сыр"),
    "syngonium-albo": ("Сингониум Альбо", "Сингоніум Альбо", "Syngonium Albo", "Сінганіум Альбо"),
    "syngonium-pink": ("Сингониум розовый|Pink", "Сингоніум рожевий", "Syngonium różowe", "Сінганіум ружовы"),
    "syngonium-white": ("Сингониум белая бабочка|White Butterfly", "Сингоніум Біла метелик", "Syngonium White Butterfly", "Сінганіум Белы матыль"),
    "trailing-aroids": ("Вьющиеся ароидные", "Ампельні ароїдні", "Pnące obrazkowate", "Ліяністыя араідныя"),
    "xanthosoma": ("Ксантосома", "Ксантосома", "Ksantosoma", "Ксантасома"),
    "asparagus-setaceus": ("Аспарагус перистый|Спаржа", "Аспарагус перистий|Спаржа", "Szparag pierzasty", "Аспарагус перыстый"),
    "birds-nest-fern": ("Асплений гнездовой|Птичье гнездо", "Асплений гніздовий", "Zanokcica gniazdowa", "Асплен гняздовы"),
    "blechnum": ("Блехнум|Древовидный папоротник", "Блехнум", "Blechnum|Żebrowiec", "Блехнум"),
    "boston-ferns": ("Нефролепис|Нефролепис возвышенный|Бостонский папоротник", "Нефролепіс", "Nefrolepis|Paproć bostońska", "Нефралепіс"),
    "button-fern": ("Пеллея круглолистная|Пуговичный папоротник", "Пеллея круглолиста", "Pellaea okrągłolistna", "Пелея кругласталістая"),
    "ferns": ("Папоротники|Папоротник", "Папороті|Папороть", "Paprocie|Paproć", "Папараць|Папараці"),
    "maidenhair-ferns": ("Адиантум|Венерин волос", "Адіантум|Венерине волосся", "Adiantum|Orlica", "Адыянтум"),
    "microsorum-diversifolium": ("Микросорум разнолистный|Лапа кенгуру", "Мікросорум", "Microsorum różnolistne|Łapa kangura", "Мікрасорум"),
    "microsorum-musifolium": ("Микросорум крокодил|Крокодиловый папоротник", "Мікросорум крокодил", "Microsorum Crocodyllus", "Мікрасорум кракадзіл"),
    "nephrolepis-cordifolia": ("Нефролепис сердцелистный|Лимонная пуговка", "Нефролепіс серцелистий", "Nefrolepis sercolistny", "Нефралепіс сэрцалісты"),
    "phlebodium": ("Флебодиум|Голубая звезда", "Флебодіум", "Phlebodium", "Флебодыум"),
    "pteris": ("Птерис|Птерис критский", "Птерис", "Pteris|Orlica", "Птэрыс"),
    "rabbits-foot-fern": ("Даваллия|Заячья лапка", "Давалія|Заяча лапка", "Dawalia|Zajęcza łapka", "Давалія"),
    "staghorn-fern": ("Платицериум|Олений рог", "Платицеріум|Олений ріг", "Platycerium|Jelenie rogi", "Платыцэрыум|Аленевы рог"),
    "areca-palm": ("Арека|Хризалидокарпус|Пальма арека", "Арека|Хризалідокарпус", "Areka|Palma areka", "Арэка"),
    "bamboo-palm": ("Хамедорея изящная|Бамбуковая пальма", "Хамедорея|Бамбукова пальма", "Chamedora|Palma bambusowa", "Хамедарэя"),
    "chamaedorea-radicalis": ("Хамедорея корневая", "Хамедорея", "Chamedora", "Хамедарэя"),
    "cycas-revoluta": ("Цикас|Саговая пальма|Цикас поникающий", "Цикас|Саговая пальма", "Cykas|Sagowiec odwinięty", "Цыкас|Сагавая пальма"),
    "european-fan-palm": ("Хамеропс|Европейская веерная пальма", "Хамеропс", "Chamerops|Palma wachlarzowa", "Хамеропс"),
    "kentia-palm": ("Ховея|Кентия", "Ховея|Кентія", "Hovea|Kentia", "Ховея|Кентыя"),
    "lady-palm": ("Раписа|Рапис|Дамская пальма", "Раписа|Рапіс", "Raphis|Palma damska", "Рапіс"),
    "majesty-palm": ("Равенея|Королевская пальма", "Равенея", "Ravenea|Palma majesty", "Равенея"),
    "palms": ("Пальмы|Пальма", "Пальми|Пальма", "Palmy|Palma", "Пальмы|Пальма"),
    "parlor-palm": ("Хамедорея|Комнатная пальма", "Хамедорея|Кімнатна пальма", "Chamedora|Palma pokojowa", "Хамедарэя"),
    "phoenix-roebelenii": ("Финик Рёбелена|Финиковая пальма", "Фінік Рьобелена|Фінікова пальма", "Daktylowiec Roebelena|Palma daktylowa", "Фінік Рэбелена"),
    "ponytail-palm": ("Бокарнея|Нолина|Бутылочная пальма", "Бокарнея|Ноліна", "Beaucarnea|Nolina|Palma butelkowa", "Бокарнея|Ноліна"),
    "adenium": ("Адениум|Пустынная роза", "Аденіум|Пустельна троянда", "Adenium|Róża pustyni", "Адэніум|Пустынная ружа"),
    "aeonium": ("Эониум", "Еоніум", "Eonium", "Эоніум"),
    "agave": ("Агава", "Агава", "Agawa", "Агава"),
    "aloe-vera": ("Алоэ вера|Алоэ настоящее", "Алое вера|Алое справжнє", "Aloes zwyczajny|Aloe vera", "Алоэ вера"),
    "aloes": ("Алоэ", "Алое", "Aloes", "Алоэ"),
    "bears-paw": ("Котиледон|Медвежья лапа", "Котиледон|Ведмежа лапа", "Cotyledon|Niedźwiedzia łapa", "Катыледон|Мядзведжая лапа"),
    "burros-tail": ("Седум Моргана|Ослиный хвост", "Седум Моргана|Ослячий хвіст", "Sedum Morgana|Ośli ogon", "Седум Моргана|Асліны хвост"),
    "crassula-ovata-minor": ("Толстянка овальная миниатюрная|Денежное дерево", "Товстянка овальна міні|Грошове дерево", "Grubosz jajowaty mini|Drzewko szczęścia", "Тоўстанка авальная|Грашовае дрэва"),
    "crassulas": ("Толстянки|Крассула", "Товстянки|Красула", "Grubosze|Crassula", "Тоўстанкі|Красула"),
    "echeveria": ("Эхеверия", "Ехеверія", "Eszeweria", "Эхеверыя"),
    "euphorbia-tirucalli": ("Молочай тирукалли|Карандашный кактус", "Молочай тируклаллі", "Wilczomlecz tirucalli|Wilczomlecz ołówkowaty", "Молачай тырукалі"),
    "euphorbia-trigona": ("Молочай трёхгранный|Африканское молочное дерево", "Молочай тригранний", "Wilczomlecz trójgraniasty", "Молачай трохгранны"),
    "euphorbias": ("Молочаи|Молочай", "Молочаї|Молочай", "Wilczomlecze|Wilczomlecz", "Молачаі|Молачай"),
    "gasteria": ("Гастерия", "Гастерія", "Gasteria", "Гастэрыя"),
    "graptopetalum": ("Граптопеталум", "Граптопеталум", "Graptopetalum", "Граптапеталум"),
    "graptoveria": ("Граптоверия", "Граптоверія", "Graptoveria", "Граптаверыя"),
    "haworthia": ("Хавортия|Хавортия полосатая", "Хаворція|Хаворція смугаста", "Haworsja|Haworthia", "Хаворція"),
    "jade-plant": ("Толстянка|Денежное дерево|Крассула|Крассула овальная", "Товстянка|Грошове дерево|Красула", "Grubosz|Drzewko szczęścia|Crassula", "Тоўстанка|Грашовае дрэва|Красула"),
    "kalanchoe-luciae": ("Каланхоэ Люции|Каланхоэ веслоподобное", "Каланхое Люції", "Kalanchoe Luciae|Kalanchoe wiosłowate", "Каланхое Люцыі"),
    "lithops": ("Литопс|Живые камни", "Літопс|Живе каміння", "Lithops|Żywe kamienie", "Літопс|Жывыя камяні"),
    "pachyphytum": ("Пахифитум", "Пахіфітум", "Pachyphytum", "Пахіфітум"),
    "portulacaria-afra": ("Портулакария|Слоновый куст|Денежное дерево", "Портулакарія|Слоновий кущ", "Portulakaria afrykańska|Krzew słoniowy", "Партулакарыя"),
    "sedum": ("Седум|Очиток", "Седум|Очиток", "Rozchodnik|Sedum", "Седум|Вялікі ачыток"),
    "sempervivum": ("Семпервивум|Молодило", "Семпервівум|Молодило", "Rojnik|Sempervivum", "Сэмпервівум|Маладзіла"),
    "senecio": ("Крестовник|Сенецио", "Хрестовник|Сенеціо", "Starzec|Senecio", "Сенэцыа|Крыжаўнік"),
    "split-rock": ("Раскалывающийся камень|Плейоспилос", "Розколений камінь", "Pęknięty kamień|Pleiospilos", "Плейяспілас"),
    "string-of-bananas": ("Сенецио Ожерелье из бананов|Нитка бананов", "Сенеціо Нитка бананів", "Starzec Sznur bananów", "Сенэцыя Нітка бананаў"),
    "string-of-dolphins": ("Сенецио Нитка дельфинов", "Сенеціо Нитка дельфінів", "Starzec Sznur delfinów", "Сенэцыя Нітка дэльфінаў"),
    "string-of-hearts": ("Церопегия Вуда|Нитка сердец", "Церопегія Вуда|Нитка сердець", "Ceropegia Wooda|Sznur serc", "Церапегія|Нітка сэрцаў"),
    "string-of-needles": ("Нитка игл", "Нитка голок", "Sznur igieł", "Нітка іголак"),
    "string-of-pearls": ("Сенецио Роули|Нитка жемчуга|Жемчужная нить", "Сенеціо Роулі|Нитка перлів", "Starzec Rowleya|Sznur pereł", "Сенэцыя Роўлі|Нітка жэмчугу"),
    "string-of-rubies": ("Нитка рубинов|Отонна", "Нитка рубінів", "Sznur rubinów", "Нітка рубінаў"),
    "string-of-turtles": ("Пеперомия Нитка черепах|Нитка черепашек", "Пеперомія Нитка черепах", "Peperomia Sznur żółwi", "Пеперомія Нітка чарапашак"),
    "succulent-aloe-v-black": ("Алоэ Black Gem", "Алое Black Gem", "Aloes Black Gem", "Алоэ Black Gem"),
    "succulent-echeveria-lola": ("Эхеверия Lola", "Ехеверія Lola", "Eszeweria Lola", "Эхеверыя Lola"),
    "succulent-echeveria-perle": ("Эхеверия Perle von Nürnberg|Жемчужина Нюрнберга", "Ехеверія Perle von Nürnberg", "Eszeweria Perle von Nürnberg", "Эхеверыя Perle von Nürnberg"),
    "succulent-haworthia-cooperi": ("Хавортия Купера", "Хаворція Купера", "Haworsja Coopera", "Хаворція Купера"),
    "succulents": ("Суккуленты|Суккулент", "Сукуленти|Сукулент", "Sukulenty|Sukulent", "Сукуленты|Сукулент"),
    "barrel-cactus": ("Бочонковидный кактус|Эхинокактус|Бочка", "Бочкоподібний кактус|Ехінокактус", "Kaktus beczkowaty|Echinokaktus", "Бочкападобны кактус|Эхінакактус"),
    "cacti": ("Кактусы|Кактус", "Кактуси|Кактус", "Kaktusy|Kaktus", "Кактусы|Кактус"),
    "christmas-cactus": ("Декабрист|Шлюмбергера|Рождественский кактус|Зигокактус", "Декабрист|Шлюмбергера|Різдвяний кактус", "Szlumbergera|Kaktus bożonarodzeniowy|Grudnik", "Дэкабрыст|Шлюмбергера|Калядны кактус"),
    "easter-cactus": ("Рипсалидопсис|Пасхальный кактус", "Ріпсалідопсис|Великодній кактус", "Rhipsalidopsis|Kaktus wielkanocny", "Рыпсалідопсіс|Вялікодны кактус"),
    "echinopsis": ("Эхинопсис|Пасхальная лилия", "Ехінопсис", "Echinopsis|Lilia wielkanocna", "Эхінапсіс"),
    "epiphyllum": ("Эпифиллум|Орхидейный кактус", "Епіфілум|Орхідейний кактус", "Epiphyllum|Kaktus orchidea", "Эпіфілум"),
    "forest-cacti": ("Лесные кактусы", "Лісові кактуси", "Kaktusy leśne", "Лясныя кактусы"),
    "gymnocalycium": ("Гимнокалициум", "Гімнокаліціум", "Gymnocalycium|Kaktus podbródkowy", "Гімнакаліцыум"),
    "mammillaria": ("Маммиллярия", "Маміларія", "Mammillaria|Kaktus poduszkowy", "Мамілярыя"),
    "old-lady-cactus": ("Маммиллярия длинная|Кактус старушка", "Маміларія довга|Кактус стара жінка", "Mammillaria|Kaktus Stara dama", "Мамілярыя доўгая"),
    "opuntia-microdasys": ("Опунция мелковолосистая|Заячьи ушки", "Опунція дрібноволоса|Заячі вушка", "Opuncja drobnokolczasta|Uszy królika", "Апунцыя"),
    "parodia": ("Пародия|Шаровидный кактус", "Пародія", "Parodia|Kaktus kulisty", "Парадыя"),
    "prickly-pear": ("Опунция|Колючая груша", "Опунція|Колюча груша", "Opuncja|Figowiec", "Апунцыя|Калючая груша"),
    "rat-tail-cactus": ("Апорокактус|Крысиный хвост", "Апорокактус|Щурячий хвіст", "Aporocactus|Kaktus szczurzy ogon", "Апарокактус"),
    "rhipsalis": ("Рипсалис", "Ріпсаліс", "Rhipsalis|Rzęsiak", "Рыпсаліс"),
    "saguaro": ("Сагуаро|Карнегия", "Сагуаро", "Saguaro|Karnegia", "Сагуара"),
    "star-cactus": ("Астрофитум|Звёздчатый кактус", "Астрофітум", "Astrophytum|Kaktus gwiazda", "Астрафітум"),
    "thanksgiving-cactus": ("Шлюмбергера усечённая|Декабрист|Кактус благодарения", "Шлюмбергера|Декабрист", "Szlumbergera ostrokątna|Grudnik", "Шлюмбергера|Дэкабрыст"),
    "african-violets": ("Сенполия|Узамбарская фиалка|Фиалка", "Сенполія|Узамбарська фіалка|Фіалка", "Fiołek afrykański|Saintpaulia", "Сэнпалія|Фіялка"),
    "begonia-angel-wing": ("Бегония Ангельские крылья|Бегония коралловая", "Бегонія Ангельські крила", "Begonia Anielskie skrzydła", "Бегонія Анёльскія крылы"),
    "begonia-ferox": ("Бегония Ферокс", "Бегонія Ферокс", "Begonia Ferox", "Бегонія Ферокс"),
    "begonia-maculata": ("Бегония пятнистая|Бегония Maculata", "Бегонія плямиста", "Begonia plamista|Begonia Maculata", "Бегонія плямістая"),
    "begonia-rex": ("Бегония Рекс|Бегония королевская", "Бегонія Рекс|Бегонія королівська", "Begonia Rex|Begonia królewska", "Бегонія Рэкс|Бегонія каралеўская"),
    "bromeliad-aechmea": ("Эхмея|Бромелия", "Ехмея|Бромелія", "Eszmea|Bromelia", "Эхмея|Бромелія"),
    "bromeliad-guzmania": ("Гузмания|Бромелия", "Гузманія|Бромелія", "Guzmania|Bromelia", "Гузманія|Бромелія"),
    "bromeliad-neoregelia": ("Неорегелия|Бромелия", "Неорегелія|Бромелія", "Neoregelia|Bromelia", "Нэарэгелія|Бромелія"),
    "bromeliads": ("Бромелии|Бромелия", "Бромелії|Бромелія", "Bromelie|Bromelia", "Бромеліі|Бромелія"),
    "clivia": ("Кливия|Клива", "Клівія", "Clivia|Kliwia", "Клівія"),
    "coffee-plant": ("Кофейное дерево|Кофе", "Кавове дерево|Кава", "Kawowiec|Kawa", "Кававае дрэва|Кава"),
    "crown-of-thorns": ("Молочай Миля|Терновый венец", "Молочай Мілія|Терновий вінець", "Wilczomlecz Milla|Korona cierniowa", "Молачай Мілі|Церняваы вянок"),
    "cyclamen": ("Цикламен|Альпийская фиалка", "Цикламен|Альпійська фіалка", "Cyklamen|Fiołek alpejski", "Цыкламен"),
    "cymbidium": ("Цимбидиум|Орхидея", "Цимбідіум|Орхідея", "Cymbidium|Storczyk", "Цымбідыум|Архідэя"),
    "gardenia": ("Гардения", "Гарденія", "Gardenia|Gardenia jaśminowata", "Гардэнія"),
    "gerbera-daisy": ("Гербера", "Гербера", "Gerbera", "Гербера"),
    "gloxinia": ("Глоксиния|Синнингия", "Глоксинія|Сіннінгія", "Gloksynia|Synningia", "Глаксінія"),
    "goldfish-plant": ("Колумнея|Золотая рыбка", "Колумнея|Золота рибка", "Kolumnea|Złota rybka", "Калумнея"),
    "hibiscus-tropical": ("Гибискус|Китайская роза", "Гібіскус|Китайська троянда", "Hibiskus|Róża chińska", "Гібіскус|Кітайская ружа"),
    "hoya-carnosa": ("Хойя мясистая|Восковой плющ|Хойя Carnosa", "Хоя м'ясиста|Воскова лоза", "Hoja gruboszowata|Woskowy bluszcz", "Хоя мяхкая|Васковы плюшч"),
    "hoya-kerrii": ("Хойя Керри|Хойя сердечко|Сердце", "Хоя Керрі|Хоя серденько", "Hoja Kerra|Serce", "Хоя Кэры|Сэрца"),
    "hoyas": ("Хойи|Хойя|Восковой плющ", "Хої|Хоя", "Hoje|Hoja", "Хоі|Хоя"),
    "ixora": ("Иксора", "Іксора", "Ixora|Iksora", "Іксора"),
    "jasmine": ("Жасмин", "Жасмин", "Jaśmin", "Язмін|Жасмін"),
    "kalanchoe": ("Каланхоэ", "Каланхое", "Kalanchoe|Żyworódka", "Каланхое"),
    "lipstick-plant": ("Эсхинантус|Помадное растение", "Ескінантус", "Eschynanthus|Szminka", "Эсхінантус"),
    "mandevilla": ("Мандевилла|Диплодения", "Мандевілла|Діпладенія", "Mandevilla|Dipladenia", "Мандэвіла"),
    "medinilla": ("Мединилла", "Медініла", "Medinilla", "Мэдыніла"),
    "paphiopedilum": ("Пафиопедилум|Венерин башмачок|Орхидея", "Пафіопедилум|Венерин черевичок", "Paphiopedilum|Obuwik|Storczyk", "Пафіяпедылум|Венерын чаравічак"),
    "phalaenopsis-orchids": ("Фаленопсис|Орхидея|Орхидея фаленопсис", "Фаленопсис|Орхідея", "Falenopsis|Storczyk|Orchidea", "Фаленопсіс|Архідэя"),
    "poinsettia": ("Пуансеттия|Рождественская звезда", "Пуансетія|Різдвяна зірка", "Poinsecja|Gwiazda betlejemska", "Пуансетыя|Калядная зорка"),
    "stephanotis": ("Стефанотис|Мадагаскарский жасмин", "Стефанотіс", "Stefanotis|Jaśmin madagaskarski", "Стэфанотыс"),
    "streptocarpus": ("Стрептокарпус|Капская примула", "Стрептокарпус", "Streptocarpus|Prymulka przylądkowa", "Стрэптакарпус"),
    "tuberous-begonias": ("Клубневая бегония", "Бульбова бегонія", "Begonia bulwiasta", "Клубневая бегонія"),
    "zebra-plant-aphelandra": ("Афеландра|Зебровое растение", "Афеландра", "Afelandra|Zebra", "Афеландра"),
    "calathea-ornata": ("Калатея украшенная|Калатея Орната", "Калатея прикрашена", "Kalatea ozdobna|Pin-stripe", "Калатэя ўпрыгожаная"),
    "calathea-zebrina": ("Калатея зебровая|Зебрина", "Калатея зебровидна", "Kalatea zebrowata", "Калатэя зебравая"),
    "calathea-rattlesnake": ("Калатея лансифолия|Гремучая змея|Rattlesnake", "Калатея Гримуча змія", "Kalatea Grzechotnik", "Калатэя Грымучая змяя"),
    "ctenanthe": ("Ктенанта|Гребенник", "Ктенанта", "Ktenanta|Ctenanthe", "Ктэнанта"),
    "maranta-prayer-plant": ("Маранта|Маранта трёхцветная|Молящееся растение", "Маранта|Молитовна рослина", "Maranta|Roślina modląca się", "Маранта"),
    "prayer-plants": ("Маранты|Маранта|Молящиеся растения", "Маранти|Маранта", "Marantowate|Maranta", "Маранты|Маранта"),
    "stromanthe": ("Строманта|Тритостар", "Строманта", "Stromanthe|Stromanta", "Стромантэ"),
    "babys-tears": ("Солейролия|Детские слёзы", "Солейролія", "Soleirolia|Łzy dziecka", "Солейролія"),
    "cissus-discolor": ("Циссус разноцветный|Бегониевый виноград", "Цисус різнобарвний", "Cissus dwubarwny", "Цысус рознакаляровы"),
    "dischidia": ("Дисхидия", "Дісхідія", "Dischidia", "Дыхідыя"),
    "english-ivy": ("Плющ обыкновенный|Английский плющ|Хедера", "Плющ звичайний|Хедера", "Bluszcz pospolity|Hedera", "Плюшч звычайны|Хедэра"),
    "grape-ivy": ("Циссус ромболистный|Виноградный плющ|Березка", "Цисус ромболистий|Березка", "Cissus rombolistny|Winorośl", "Цысус|Бярозка"),
    "hoya-australis": ("Хойя южная|Хойя Australis", "Хоя південна", "Hoja południowa", "Хоя паўднёвая"),
    "hoya-krimson-queen": ("Хойя Krimson Queen|Хойя Королева", "Хоя Krimson Queen", "Hoja Krimson Queen", "Хоя Krimson Queen"),
    "purple-passion-plant": ("Гинура плетистая|Пурпурный бархат", "Гінура|Пурпурний оксамит", "Gynura|Purpurowy aksamit", "Гінура"),
    "scindapsus": ("Сциндапсус", "Сциндапсус", "Scindapsus", "Сцындапсус"),
    "scindapsus-pictus": ("Сциндапсус пёстрый|Сатиновый потос|Pictus", "Сциндапсус строкатий", "Scindapsus pstry", "Сцындапсус стракаты"),
    "swedish-ivy": ("Плектрантус|Шведский плющ|Комнатная мята", "Плектрантус|Шведський плющ", "Plektrantus|Szwedzki bluszcz", "Плектрантус"),
    "tradescantia": ("Традесканция", "Традесканція", "Trzykrotka", "Традэсканцыя"),
    "tradescantia-purple-heart": ("Сеткреазия пурпурная|Пурпурное сердце", "Сеткреазія пурпурова", "Setcreasea|Fioletowe serce", "Сеткрэазія"),
    "vines-trailing": ("Вьющиеся и ампельные растения|Лианы", "Ліани та ампельні рослини", "Pnącza i rośliny zwisające", "Ліяны і ампельныя расліны"),
    "fiddle-leaf-fig": ("Фикус лировидный|Фикус Лирата", "Фікус ліроподібний|Фікус Ліата", "Figowiec lirolistny|Fikus lirata", "Фікус ліраідны"),
    "ficus-benjamina": ("Фикус Бенджамина|Фикус бенджамина|Плакучий фикус", "Фікус Бенджаміна|Плакучий фікус", "Fikus Beniamina|Figowiec benjamina", "Фікус Бенджаміна"),
    "ficus-microcarpa": ("Фикус микрокарпа|Женьшеневый фикус|Китайский баньян", "Фікус мікрокарпа|Женьшеневий фікус", "Fikus mikrokarpa|Figowiec mikrokarpa", "Фікус мікракарпа"),
    "ficus-elastica-tineke": ("Фикус каучуконосный Тинеке|Тинеке", "Фікус каучуконосний Тінеке", "Fikus sprężysty Tineke", "Фікус каўчуканосны Цінэке"),
    "rubber-plant": ("Фикус каучуконосный|Фикус эластика|Каучуковое дерево", "Фікус каучуконосний|Фікус еластика", "Figowiec sprężysty|Fikus elastica|Kauczukowiec", "Фікус каўчуканосны|Фікус эластыка"),
    "rubber-plant-burgundy": ("Фикус эластика Бургунди|Burgundy", "Фікус еластика Бургунді", "Fikus sprężysty Burgundy", "Фікус эластыка Бургундзі"),
    "rubber-plant-ruby": ("Фикус эластика Руби|Ruby", "Фікус еластика Рубі", "Fikus sprężysty Ruby", "Фікус эластыка Рубі"),
    "bird-of-paradise": ("Стрелиция|Райская птица", "Стрелітція|Райський птах", "Strelicja|Rajski ptak", "Стрэліцыя|Райская птушка"),
    "bird-of-paradise-white": ("Стрелиция белая|Райская птица белая", "Стрелітція біла", "Strelicja biała", "Стрэліцыя белая"),
    "bonsai": ("Бонсай", "Бонсай", "Bonsai", "Бонсай"),
    "coleus": ("Колеус|Плектрантус|Крапива комнатная", "Колеус|Кімнатна кропива", "Pokrzywa|Koleus", "Калеус"),
    "cordyline": ("Кордилина", "Кордиліна", "Cordyline|Buzułka", "Кардыліна"),
    "corpse-flower": ("Аморфофаллус|Трупный цветок", "Аморфофалус", "Amorphophallus|Kwiat trupi", "Амарфафалус"),
    "croton": ("Кротон|Кодиеум", "Кротон|Кодіеум", "Kroton|Kodiaeum", "Кратон|Кадыеум"),
    "croton-mammy": ("Кротон Mammy|Кодиеум Мамми", "Кротон Mammy", "Kroton Mammy", "Кратон Mammy"),
    "cycads": ("Саговники|Цикасы", "Саговники", "Sagowce|Cykasy", "Сагоўнікі|Цыкасы"),
    "fatsia": ("Фатсия|Японская аралия", "Фатсія|Японська арлія", "Fatsja japońska|Aralia japońska", "Фатсія"),
    "geogenanthus": ("Геогенантус|Серебряная змея", "Геогенантус", "Geogenanthus", "Геагенантус"),
    "heliconia": ("Геликонии|Лобстер клешня", "Геліконія", "Heliconia|Helikonia", "Геліконія"),
    "lemon-cypress": ("Кипарис лимонный|Купрессус", "Кипарис лимонний", "Cyprys cytrynowy", "Кіпарыс цытрынавы"),
    "lucky-bamboo": ("Драцена Сандера|Бамбук счастья|Лаки бамбук", "Драцена Сандера|Бамбук щастя", "Dracena Sandera|Bambus szczęścia", "Драцэна Сандэра|Бамбук шчасця"),
    "money-tree": ("Пахира|Денежное дерево", "Пахіра|Грошове дерево", "Pachira|Drzewko szczęścia|Kasztan wodny", "Пахіра|Грашовае дрэва"),
    "nerve-plant": ("Фиттония|Нервюрное растение", "Фіттонія", "Fittonia|Roślina nerwowa", "Фітонія"),
    "norfolk-island-pine": ("Араукария|Комнатная ель", "Араукарія|Кімнатна ялинка", "Araukaria|Choinka pokojowa", "Араўкарыя|Пакаёвая елка"),
    "oxalis": ("Кислица|Оксалис", "Квасениця|Оксаліс", "Szczawik|Oxalis", "Кіслiца|Оксаліс"),
    "oxalis-triangularis": ("Кислица треугольная|Оксалис фиолетовый|Бабочка", "Квасениця трикутна|Оксаліс фіолетовий", "Szczawik trójkątny|Fioletowa koniczyna", "Кіслiца трохвугольная"),
    "peperomia": ("Пеперомия", "Пеперомія", "Peperomia", "Пеперомія"),
    "peperomia-caperata": ("Пеперомия морщинистая|Изумрудная рябь", "Пеперомія зморшкувата", "Peperomia pomarszczona", "Пеперомія маршчыністая"),
    "peperomia-obtusifolia": ("Пеперомия туполистная|Детское каучуковое", "Пеперомія тупоголова", "Peperomia tępolistna", "Пеперомія тупалістая"),
    "peperomia-watermelon": ("Пеперомия арбузная|Watermelon", "Пеперомія кавунова", "Peperomia arbuzowa", "Пеперомія кавунавая"),
    "pilea": ("Пилея", "Пілея", "Pilea", "Пілея"),
    "pilea-chinese-money-plant": ("Пилея пеперомиевидная|Китайское денежное дерево|Денежное дерево", "Пілея пеперомієвидна|Грошове дерево", "Pilea peperomioides|Pilea Peperomiowata|Chińska roślina pieniężna", "Пілея пеперамiевiдная|Грашовае дрэва"),
    "pilea-involucrata": ("Пилея кадье|Дружеское растение", "Пілея Кадьє", "Pilea okazała|Roślina przyjaźni", "Пілея"),
    "podocarpus": ("Подокарпус|Буддийская сосна", "Подокарпус", "Podocarpus|Kroplik", "Падакарпус"),
    "polka-dot-plant": ("Гипестес|Горошек|Веснушка", "Гіпестес", "Hipoestes|Kropkowata roślina", "Гіпэстэс"),
    "welwitschia": ("Вельвичия", "Вельвічія", "Welwitschia", "Вэльвічыя"),
    "butterwort": ("Жирянка", "Жирянка", "Tłustosz", "Жыраўка"),
    "carnivorous-plant": ("Хищное растение|Насекомоядное растение", "Хижа рослина|Комахоїдна рослина", "Roślina owadożerna|Mięsożerna", "Драпежная расліна"),
    "cobra-lily": ("Дарлингтония|Лилия кобры", "Дарлінгтонія", "Darlingtonia", "Дарлінгтонія"),
    "drosera-capensis": ("Росянка капская", "Росичка капська", "Rosiczka przylądkowa", "Расіца капская"),
    "nepenthes": ("Непентес|Кувшинник", "Непентес|Глечикоподібна рослина", "Nepenthes|Dzbanecznik", "Непентэс"),
    "pitcher-plant": ("Саррацения|Кувшинник|Непентес", "Сараценія|Глечикоподібна", "Saracenia|Dzbanecznik", "Сарацэнія"),
    "sarracenia": ("Саррацения", "Сараценія", "Saracenia", "Сарацэнія"),
    "sundew": ("Росянка", "Росичка", "Rosiczka", "Расіца"),
    "venus-flytrap": ("Венерина мухоловка|Диониея", "Венерина мухоловка|Діонея", "Muchołówka|Dionaea", "Венерына мухалоўка|Дыянея"),
    "air-plants": ("Тилландсия|Воздушное растение", "Тілландсія|Повітряна рослина", "Tillandsia|Roślina powietrzna", "Тыландсія|Паветраная расліна"),
    "dendrobium-orchid": ("Дендробиум|Орхидея", "Дендробіум|Орхідея", "Dendrobium|Storczyk", "Дэндрабіум|Архідэя"),
    "epiphytes": ("Эпифиты", "Епіфіти", "Epifity", "Эпіфіты"),
    "lichen": ("Лишайник", "Лишайник", "Porost", "Лішайнік"),
    "mosses": ("Мох|Мхи", "Мох|Мохи", "Mech|Mchy", "Мох|Мхі"),
    "oncidium-orchid": ("Онцидиум|Орхидея", "Онцидіум|Орхідея", "Oncidium|Storczyk", "Анцыдыум|Архідэя"),
    "orchids": ("Орхидеи|Орхидея", "Орхідеї|Орхідея", "Storczyki|Orchidea|Storczyk", "Архідэі|Архідэя"),
    "resurrection-fern": ("Папоротник воскресающий|Плеопельтис", "Папороть воскресна", "Paproć zmartwychwstająca", "Папараць уваскрасаючая"),
    "spanish-moss": ("Тилландсия уснеевидная|Испанский мох", "Тілландсія уснеєвидна|Іспанський мох", "Tillandsia brodaczkowata|Mech hiszpański", "Тыландсія|Іспанскі мох"),
    "tillandsia-xerographica": ("Тилландсия ксерографика", "Тілландсія ксерографіка", "Tillandsia xerographica", "Тыландсія ксерографіка"),
    "vanda-orchid": ("Ванда|Орхидея", "Ванда|Орхідея", "Vanda|Storczyk", "Ванда|Архідэя"),
    "vanilla": ("Ваниль|Орхидея ваниль", "Ваніль", "Wanilia", "Ваніль"),
}


def main() -> None:
    root = Path(__file__).resolve().parent.parent
    catalog = json.loads((root / "app/src/main/assets/plant_catalog.json").read_text(encoding="utf-8"))
    ids = [p["id"] for p in catalog if p["category"].startswith("Houseplants")
           or p["category"] in ("Specialty - Carnivorous", "Specialty - Epiphytes & Moss")]
    names: dict[str, dict[str, str]] = {}
    for plant_id in ids:
        if plant_id in EXPLICIT:
            values = EXPLICIT[plant_id]
        else:
            prefix, _, rest = plant_id.partition("-")
            genus = GENUS.get(prefix)
            if not genus or not rest:
                continue
            values = tuple(f"{g} {title(rest)}" for g in genus)
        assert len(values) == 4, plant_id
        values = tuple(fix_lookalikes(v) for v in values)
        names[plant_id] = dict(zip(LANGS, values))
    missing = [i for i in ids if i not in names]
    target = root / "app/src/main/assets/plant_names.json"
    target.write_text(json.dumps(names, ensure_ascii=False, indent=0, separators=(",", ":")), encoding="utf-8")
    print(f"{len(names)} named of {len(ids)} houseplants; missing: {missing}")


if __name__ == "__main__":
    main()
