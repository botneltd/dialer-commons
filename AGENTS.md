# AGENTS.md

Tämä on yhden repon ohje AI-koodausagenteille. Tiedosto pysyy lyhyenä ja koskee vain tätä repoa. Tuotteen järjestelmäkuvausta ei kirjoiteta tähän.

## Yhteiset ohjeet

Lue `docs/AGENTS-yhteiset.md` ja noudata sitä ennen toteutusta. Se on synkronoitu kopio brändirepon `botneltd/brand` tiedostosta `kehitys/AGENTS-yhteiset.md`. Kopion pitää vastata lähdettä tavuittain.

Yhteiset säännöt muutetaan brändirepossa. Tuote-repo päivittää kopion omassa PR:ssä sen jälkeen, kun lähde on `main`-haarassa.

Jos tämä tiedosto ja yhteinen ohje ovat ristiriidassa, yhteinen ohje voittaa. Ristiriita kirjataan PR:ään.

## Tuotteen järjestelmäkuvaus

Ennen muutoksia lue brändirepon tuotekuvaus `kehitys/tuotteet/hemmo-hello-ai.md`.

Tuotekuvaus kertoo, miten tuotteet jakavat komponentteja ja mikä projekti omistaa skeeman. Kuvausta ei kopioida tähän tiedostoon. Jos tuotekuvausta ei näy, sitä ei keksitä.

## Tämä repo

- Tuote: Hello AI.
- Tämän repon paikka tuotekokonaisuudessa: Android-apukirjasto, josta Hello AI -client riippuu. Hemmoa ei ole tässä.
- Repokohtaiset komennot: testi-CI:tä ei ole. `.github/` sisältää vain `dependabot.yml`. `jitpack.yml` ei aja testejä. `src/test`- eikä `src/androidTest`-tiedostoja ole.
- Repokohtaiset säännöt:
  - Versio on `6.3.1`, ryhmä `com.goodwy` (`commons/build.gradle.kts`). Client viittaa koordinaattiin `com.github.botneltd:dialer-commons`.
  - `local_contacts.db` (`commons/src/main/kotlin/com/goodwy/commons/databases/ContactsDatabase.kt`) on puhelimen kanta, ei tuotteen Postgres.
  - `launchCallIntent` tuntee paketin `com.sorwi.aidialer` (`commons/src/main/kotlin/com/goodwy/commons/extensions/Activity.kt`).
  - Juuren `build.gradle.kts` osoittaa puuttuvaan `config/detekt/`-polkuun. Moduulin Detekt osoittaa juuren `detekt.yml`:iin.
  - Kustannusraja per puhelu: `EI_VIELÄ_SOVITTU`. Tämä repo ei mittaa puhelua.
