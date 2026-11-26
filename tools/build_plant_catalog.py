"""Builds app/src/main/assets/plant_catalog.json from the Luminoid common plants dataset.

Source: https://github.com/Luminoid/plantfolio-common-plants (CC BY-NC-SA 4.0, personal non-commercial use).
Download dist/common_plants.json and run:

    python -I tools/build_plant_catalog.py path/to/common_plants.json

Only the fields the app reads are kept. The dataset is English; localized names live in
app/src/main/assets/plant_names.json (written by hand, merged by the app at load time).
"""
import json
import sys
from pathlib import Path

KEEP = [
    "id", "typeName", "description", "commonExamples", "careTips", "origin",
    "springInterval", "summerInterval", "fallInterval", "winterInterval",
    "lightPreference", "humidityPreference", "temperaturePreference", "plantToxicity",
    "soilPhPreference", "drainagePreference", "wateringMethod", "plantLifeSpan",
    "category", "hardinessZones", "growthRate", "propagationMethods",
]


def main() -> None:
    source = Path(sys.argv[1])
    data = json.loads(source.read_text(encoding="utf-8"))
    plants = [{key: plant.get(key) for key in KEEP} for plant in data if "id" in plant]
    target = Path(__file__).resolve().parent.parent / "app" / "src" / "main" / "assets" / "plant_catalog.json"
    target.parent.mkdir(parents=True, exist_ok=True)
    target.write_text(json.dumps(plants, ensure_ascii=False, separators=(",", ":")), encoding="utf-8")
    print(f"{len(plants)} plants -> {target} ({target.stat().st_size // 1024} KB)")


if __name__ == "__main__":
    main()
