"""Builds the translated texts of the plant guide.

Outputs (in app/src/main/assets/):
  plant_phrases.json       {"English care-tip sentence": {"ru": "...", "uk": "...", "pl": "...", "be": "..."}}
  plant_descriptions.json  {"plant id": {"ru": "...", ...}}   (whole descriptions of the most popular houseplants)

Inputs (in tools/): plant_phrases_tr.txt (translations of the 300 most frequent sentences, numbered by the
ranking below) and plant_descriptions_tr.txt. The app splits a plant's care tips into sentences with the same
rule as `sentences()` here and translates every sentence it finds in the phrase table; the others stay English.

Run from the repository root:  python -I tools/build_plant_phrases.py
"""
import collections
import json
import re
from pathlib import Path

LANGS = ("ru", "uk", "pl", "be")
TOP = 300
LOOKALIKES = str.maketrans("iaceopxyvEHKMOPTX", "іасеорхувЕНКМОРТХ")


def sentences(text: str) -> list[str]:
    """The sentence rule the app also uses: split after . ! ? followed by whitespace."""
    return [part.strip() for part in re.split(r"(?<=[.!?])\s+", text) if part.strip()]


def is_house(plant: dict) -> bool:
    return plant["category"].startswith("Houseplants") or plant["category"] in (
        "Specialty - Carnivorous", "Specialty - Epiphytes & Moss")


def fix(text: str) -> str:
    """Latin look-alike letters inside a word that has Cyrillic letters are typos: make them Cyrillic."""
    def fix_token(token: str) -> str:
        has_cyrillic = any("Ѐ" <= ch <= "ӿ" for ch in token)
        return token.translate(LOOKALIKES) if has_cyrillic else token
    return "".join(fix_token(token) for token in re.split(r"(\s+)", text))


def read_table(path: Path) -> dict[str, tuple[str, ...]]:
    table: dict[str, tuple[str, ...]] = {}
    for line in path.read_text(encoding="utf-8").splitlines():
        if not line.strip() or line.startswith("#"):
            continue
        key, _, rest = line.partition(" = ")
        parts = tuple(fix(part.strip()) for part in rest.split(" ; "))
        assert len(parts) == 4, line
        table[key.strip()] = parts
    return table


def main() -> None:
    root = Path(__file__).resolve().parent.parent
    assets = root / "app" / "src" / "main" / "assets"
    catalog = json.loads((assets / "plant_catalog.json").read_text(encoding="utf-8"))

    counts: collections.Counter = collections.Counter()
    for plant in catalog:
        weight = 3 if is_house(plant) else 1
        for sentence in sentences(plant["careTips"]):
            counts[sentence] += weight
    top = [sentence for sentence, _ in counts.most_common(TOP)]

    translations = read_table(root / "tools" / "plant_phrases_tr.txt")
    phrases = {}
    for number, sentence in enumerate(top, start=1):
        values = translations.get(str(number))
        if values:
            phrases[sentence] = dict(zip(LANGS, values))
    # extra sentences keyed by their English text (so the plants of plant_descriptions_tr.txt are fully translated)
    all_sentences = {s for plant in catalog for s in sentences(plant["careTips"])}
    for line in (root / "tools" / "plant_phrases_extra_tr.txt").read_text(encoding="utf-8").splitlines():
        if not line.strip() or line.startswith("#"):
            continue
        english, _, rest = line.partition(" || ")
        parts = tuple(fix(part.strip()) for part in rest.split(" ; "))
        assert len(parts) == 4, line
        assert english in all_sentences, f"not a sentence of the catalog: {english}"
        phrases[english] = dict(zip(LANGS, parts))
    (assets / "plant_phrases.json").write_text(
        json.dumps(phrases, ensure_ascii=False, separators=(",", ":")), encoding="utf-8")

    ids = {plant["id"] for plant in catalog}
    descriptions = {}
    for plant_id, values in read_table(root / "tools" / "plant_descriptions_tr.txt").items():
        assert plant_id in ids, plant_id
        descriptions[plant_id] = dict(zip(LANGS, values))
    (assets / "plant_descriptions.json").write_text(
        json.dumps(descriptions, ensure_ascii=False, separators=(",", ":")), encoding="utf-8")

    total = sum(len(sentences(p["careTips"])) for p in catalog)
    covered = sum(1 for p in catalog for s in sentences(p["careTips"]) if s in phrases)
    print(f"{len(phrases)} phrases ({100 * covered / total:.1f}% of all tip sentences), {len(descriptions)} descriptions")


if __name__ == "__main__":
    main()
