# Botnen yhteiset kehitys- ja testiohjeet

**Versio:** 1.3, 4.11.2026
**Koskee:** Hemmoa, Dictatoria ja tulevia Botne Oy:n tuotteita. Jokaisen tuote-repon AI-koodausagentti noudattaa tätä.

Lähde on brändirepo `botneltd/brand`, tämä tiedosto. Tuote-repo pitää kopion polussa `docs/AGENTS-yhteiset.md`, koska cloud agent ei välttämättä näe brändirepoa. Kopion PITÄÄ vastata tätä tiedostoa tavuittain. Tuotteen järjestelmäkuvaus on `kehitys/tuotteet/<tuote>.md`. Tuote-repon oma `AGENTS.md` pysyy lyhyenä ja koskee vain sitä repoa. Kumpaakaan ei kirjoiteta tähän tiedostoon. Käyttöönotto on tiedostossa `kehitys/README.md`.

**PITÄÄ** on ehdoton vaatimus. **EI SAA** on ehdoton kielto.

## 1. Spec ensin

Ei-triviaali muutos on muutos käyttäytymiseen, rajapintaan, dataan, tietoturvaan, promptiin, malliin, tooliin, konfiguraatioon tai julkiseen tekstiin. Mekaaninen yhden rivin korjaus, joka ei muuta toimintaa, on triviaali.

Ei-triviaalissa muutoksessa PITÄÄ olla ennen toteutusta kolme asiaa:

1. Spec. Mitä käyttäjälle muuttuu, ja mikä pysyy ennallaan.
2. Suunnitelma. Miten muutos tehdään.
3. Tehtävälista.

Ne kirjoitetaan PR-kuvaukseen tai docs-tiedostoon samassa muutoksessa. Oletukset PITÄÄ merkitä näkyviin. Oletus ei ole päätös. Jos oletus koskee maksuja, henkilötietoja, authia, infrastruktuuria tai asiakaspuhelua, työ pysähtyy siihen ja oletus jää PR:ään ihmisen ratkaistavaksi.

Triviaali korjaus ei tarvitse speciä. Sen PITÄÄ silti säilyttää olemassa oleva toiminta.

## 2. Pienet PR:t

- PR tekee yhden asian. Yksi ihminen pystyy tarkastamaan sen.
- Muu siivous ja refaktorointi tehdään omassa PR:ssä.
- Testiä EI SAA heikentää tai poistaa, jotta CI menee läpi. Väärä testi korjataan, ja PR kertoo miksi testi oli väärä.
- CI:n PITÄÄ olla vihreä ennen mergeä.
- PR-kuvaus täyttää kohdan 9 pohjan.

## 3. Pakolliset tietoturvatarkistukset

- Salaisuuksien skannaus työkalulla gitleaks PITÄÄ ajaa jokaisessa PR:ssä ja jokaisessa pushissa `main`-haaraan. Valmis workflow on `kehitys/templates/gitleaks.yml`. Organisaatiorepo tarvitsee salaisuuden `GITLEAKS_LICENSE`. Ilmainen lisenssi haetaan osoitteesta [gitleaks.io](https://gitleaks.io). Tämän ohjeen mukainen toteutus on GitHub Action `gitleaks/gitleaks-action@v3` ja `actions/checkout@v6` (`fetch-depth: 0`).
- Riippuvuuksien tietoturvaskannaus PITÄÄ ajaa siellä, missä ekosysteemille on skanneri. Esimerkkejä ovat Dependabot, `npm audit` ja OSV-Scanner. Korkea tai kriittinen löydös estää mergen. Poikkeus kirjataan PR:ään, ja Jukka Tuohimaa hyväksyy sen.
- Salaisuudet ja avaimet pysyvät pois reposta, lokeista ja fixtureista. Henkilötiedot, puhelinnumerot ja tallenteet noudattavat kohtaa 10.
- Jokaisella uudella endpointilla PITÄÄ olla tunnistautuminen ja pääsynhallinta (access control). Julkinen endpoint on poikkeus: se nimetään specissä, ja PR kertoo, mitä se palauttaa ja miksi tunnistautumista ei ole.

## 4. Riskiin perustuva kuri

Prototyyppi saa edetä nopeasti vain matalalla tasolla. Jos työ koskee korkean tason aluetta, korkean tason näyttö pätee, myös prototyypissä ja pienessä diffissä.

| Taso | Milloin | Näyttö ennen mergeä |
|---|---|---|
| Matala | Prototyyppi tai sisäinen kokeilu, joka ei koske maksuja, henkilötietoja, authia, infrastruktuuria eikä asiakaspuheluita | PR-kuvaus. Olemassa oleva toiminta säilyy. |
| Normaali | Muu tuotekehitys | Spec, suunnitelma ja tehtävälista. Automaattiset testit. Review. |
| Korkea | Maksut, henkilötiedot, auth, infrastruktuuri tai asiakaspuhelut | Spec, testit, review ja seuranta (monitoring). PR kertoo, mitä tuotannossa seurataan ja mistä häiriö näkyy. Puuttuva seuranta lisätään samassa muutoksessa, tai PR toteaa puutteen. |

Infrastruktuuria ovat esimerkiksi deploy, IAM, DNS, salaisuuksien hallinta ja tuotantokonfiguraatio.

Korkean riskin muutoksessa pätee myös kohta 12: feature flag tai peruutus yhdellä komennolla.

Agenttituotteessa promptin, mallin, toolin tai konfiguraation muutos vaatii kohdan 5 eval-tulokset, vaikka taso olisi muuten normaali. Jos sama muutos kuuluu korkeaan tasoon, korkean tason näyttö pätee sen lisäksi.

## 5. Agenttituotteen eval

Tuote, jossa on AI-agentti, tarvitsee kooditestien lisäksi eval-sarjan. Näitä tuotteita ovat ainakin Hemmo (puhelinavustaja) ja Dictator (sanelu ja reaaliaikainen käännös).

Eval-sarja PITÄÄ sisältää edustavia ja ongelmallisia tapauksia. Esimerkkejä: puhelut, sanelut, kielet ja reunatapaukset, kuten keskeytys, hiljaisuus, taustamelu, väärä kieli, kesken jäävä lause ja erikoissanasto. Tuotekohtainen osio tarkentaa pakolliset tapaukset.

Eval PITÄÄ ajaa jokaisen prompt-, malli-, tool- tai konfiguraatiomuutoksen jälkeen. PR kirjaa, mitä ajettiin, läpäisseiden ja hylättyjen määrän, uudet regressiot ja linkin ajoon, jos sellainen on.

Promptit, malliversio ja asetukset elävät omina tiedostoinaan (kohta 11). Niiden muutos laukaisee tämän eval-ajon.

## 6. Tuotantovirheestä regressioeval

Kun agentti toimii väärin tuotannossa, sama tapaus PITÄÄ lisätä eval-sarjaan samassa muutoksessa kuin korjaus. Korjaus pysyy näin korjauksena.

Kustannus per puhelu tai sanelu PITÄÄ kirjata, kun muutos voi muuttaa sitä. Tällaisia muutoksia ovat ainakin malli, promptin pituus ja tool-kutsujen määrä. PR vertaa lukua aiempaan, jos aiempi luku on olemassa. Nousu PITÄÄ näkyä, jotta toimiva ominaisuus ei kallistu huomaamatta. Jos kustannus nousee yli tuotteen rajan, pätee kohta 14.

## 7. Brändi ja julkinen teksti

Julkisen tekstin muutos noudattaa `viestinta/spec.md`-tiedostoa. Kohta 9 pätee koneille näkyvään tekstiin.

Kun julkista sivutekstiä muutetaan, sama väite PITÄÄ päivittää samassa muutoksessa jokaiseen paikkaan, jossa se näkyy koneille:

- sivun title
- meta description
- Open Graph
- Twitter-kuvaus
- `llms.txt`
- `llms-full.txt`
- JSON-LD ja FAQ-skeema
- manifestin kuvaus
- saman sivun muut kielet

Vanhaa lausetta EI SAA jättää voimaan yhteenkään näistä. Jos malli toistaisi poistetun lauseen, tiedostossa PITÄÄ kieltää sen käyttö.

Julkisessa tekstissä EI SAA nimetä kolmannen osapuolen tekoälypalvelua tai mallia. Viestintäspecin kohdat 3.2 ja 4.6 pätevät: toteutusta ja käytettyä mallia ei kuvata julkisessa tekstissä.

Muu viestintäspec pätee samassa muutoksessa. Englanninkielinen yritysnimi on **Botne Ltd.** Piste kuuluu nimeen. Suomenkielinen nimi on Botne Oy.

## 8. Prosessi

- Agentti avaa vain PR:n. Agentti EI SAA mergeä.
- Jukka Tuohimaa tarkastaa ja mergeää.
- Olemassa olevan toiminnan PITÄÄ säilyä. Jos tuotteet jakavat komponentin, muutos yhteen tuotteeseen ei saa rikkoa toista. Regressiotestit kattavat jokaisen tuotteen, joka jakaa komponentin.
- Jos tehtävä ja tämä ohje ovat ristiriidassa, tämä ohje voittaa. Ristiriita kirjataan PR:ään.

## 9. Näyttö jokaisessa PR:ssä

Jokaisen PR:n kuvaus käyttää pohjaa `kehitys/templates/pull_request_template.md`. Tuote-repo kopioi sen polkuun `.github/pull_request_template.md`, jotta GitHub täyttää uuden PR:n kuvauksen.

Kuvaus kertoo kolme asiaa:

1. Mitä testattiin, ja mikä oli tulos.
2. Mitkä evalit ajettiin, ja niiden tulos: läpäisseet, hylätyt, regressiot. Jos evalia ei ajettu, kuvaus kertoo miksi.
3. Mitä ei testattu. Kohtaa ei jätetä tyhjäksi. Jos aukkoa ei ole, siihen kirjoitetaan: ei jäänyt testaamatta.

Sääntö koskee jokaista PR:ää, myös triviaalia korjausta. Triviaali korjaus ei tarvitse speciä (kohta 1), mutta näyttö täytetään.

## 10. Henkilötiedot ja tallenteet

Testit, fixturet ja eval-sarja käyttävät vain anonymisoitua tai synteettistä puhelua ja sanelua. Tämä on GDPR-vaatimus.

Oikea asiakasdata, puhelinnumero ja tallenne EI SAA päätyä repoon, lokeihin tai fixtureihin. Sama koskee eval-aineistoa.

Anonymisoitu aineisto on sellaista, josta henkilöä ei voi tunnistaa. Jos anonymisointia ei voi osoittaa, aineisto kirjoitetaan synteettisenä. Lokiin ei kirjoiteta puhelun tai sanelun sisältöä, numeroa tai tallennetta.

## 11. Promptit ja malliasetukset versiohallinnassa

Hemmon ja Dictatorin promptit, malliversio ja asetukset PITÄÄ olla omina tiedostoinaan repossa. Niitä EI SAA jättää vain kovakoodatuksi sovelluslogiikan sekaan. Hakemisto nimetään tuote-repon omassa `AGENTS.md`-tiedostossa.

Muutos näihin tiedostoihin on näkyvä PR. Se laukaisee kohdan 5 eval-sarjan. Tulos kirjataan PR-kuvaukseen (kohta 9).

## 12. Peruttavuus

Korkean riskin muutos (kohta 4) PITÄÄ toimittaa feature flagin takana, tai se PITÄÄ voida perua yhdellä komennolla. PR nimeää flagin tai kirjoittaa komennon. Monen vaiheen käsiajo ei täytä yhden komennon vaatimusta.

Tietokantamigraatiolla PITÄÄ olla palautuspolku (rollback) samassa muutoksessa. PR kirjoittaa komennon, jolla palautus ajetaan. Migraatiota EI SAA mergeä ilman palautuspolkua. Migraatio tehdään vain projektissa, joka omistaa skeeman. Tuotekuvaus `kehitys/tuotteet/<tuote>.md` nimeää projektin.

## 13. Agentin rajat

Koodausagentti EI SAA muuttaa seuraavia, ellei tehtävä nimeä kohdetta erikseen:

- CI-asetukset, mukaan lukien workflowt, vaaditut tarkistukset ja niiden oikeudet
- branch protection ja ruleset
- salaisuudet, avaimet ja ympäristöjen salaisuudet
- tuotantoympäristö, mukaan lukien deploy, tuotantokonfiguraatio ja tuotantodata

Yleinen tehtävä ei riitä. Tehtävän PITÄÄ nimetä kohde, esimerkiksi "päivitä gitleaks-workflow" tai "aja tämä migraatio tuotantoon". Jos tehtävä nimeää kohteen, muutos on korkea riski.

## 14. Kustannusraja

Tuote-repon `AGENTS.md` kertoo rajan per puhelu tai sanelu. Paikkamerkki on `EI_VIELÄ_SOVITTU`. Se korvataan sovitulla rajalla.

Jos mitattu kustannus nousee rajan yli, PR merkitään erillistä hyväksyntää varten. Otsikkoon lisätään `[kustannusraja ylittyy]`. Jukka Tuohimaa hyväksyy kustannuksen erikseen ennen mergeä.

Kun paikkamerkki on vielä `EI_VIELÄ_SOVITTU`, mitattu nousu merkitään samalla tavalla. Jos kustannusta ei voi mitata, PR sanoo sen. Lukua ei keksitä.

## Tarkistus ennen PR:ää

- [ ] Ei-triviaalissa muutoksessa on spec, suunnitelma, tehtävälista ja merkityt oletukset
- [ ] PR tekee yhden asian
- [ ] Testejä ei ole heikennetty tai poistettu CI:n takia
- [ ] gitleaks ja saatavilla oleva riippuvuusskannaus ovat mukana
- [ ] PR-kuvaus käyttää kohdan 9 pohjaa: testattu, evalien tulokset ja testaamaton
- [ ] Testit, fixturet ja evalit ovat anonymisoitua tai synteettistä aineistoa
- [ ] Repossa, lokeissa ja fixtureissa ei ole salaisuuksia, avaimia, henkilötietoja, numeroita tai tallenteita
- [ ] Uudella endpointilla on tunnistautuminen ja pääsynhallinta, tai julkinen poikkeus on specissä
- [ ] Riskitason näyttö on PR:ssä. Korkeassa riskissä on myös seuranta
- [ ] Agenttituotteessa prompt-, malli-, tool- tai konfiguraatiomuutos sisältää eval-tulokset
- [ ] Tuotantovirheestä on regressioeval, jos korjaus lähti siitä
- [ ] Promptin, malliversion tai asetuksen muutos on omana tiedostona, ja eval on ajettu
- [ ] Korkean riskin muutos on feature flagin takana tai peruttavissa yhdellä komennolla
- [ ] Migraatiolla on rollback, tai muutoksessa ei ole migraatiota
- [ ] CI-asetuksia, branch protectionia, rulesetejä, salaisuuksia ja tuotantoympäristöä ei muutettu, ellei tehtävä nimennyt kohdetta
- [ ] Kustannus per puhelu tai sanelu on kirjattu, jos muutos voi muuttaa sitä
- [ ] Kustannusraja ei ylity, tai otsikossa on `[kustannusraja ylittyy]`
- [ ] Julkinen teksti on päivitetty jokaiseen koneille näkyvään paikkaan
- [ ] Julkisessa tekstissä ei ole kolmannen osapuolen tekoälypalvelun tai mallin nimeä
- [ ] Olemassa oleva toiminta säilyy. Jaettua komponenttia käyttävät tuotteet on regressiotestattu
- [ ] Migraatio on skeeman omistavassa projektissa, tai muutoksessa ei ole migraatiota
- [ ] PR on auki. Merge jää Jukalle

## Muutos

| Versio | Päivä | Muutos |
|---|---|---|
| 1.0 | 3.11.2026 | Ensimmäinen versio. |
| 1.1 | 3.11.2026 | Näyttö jokaiseen PR:ään, henkilötiedot ja tallenteet, promptit versiohallinnassa, peruttavuus, agentin rajat, kustannusraja. |
| 1.2 | 4.11.2026 | Hemmon ja Hello AI:n yhteinen tausta: muutos toiseen ei saa rikkoa toista, ja molemmat regressiotestataan. |
| 1.3 | 4.11.2026 | Sääntö yleistettiin: jaettu komponentti ei saa rikkoutua, regressiotestit kattavat kaikki jakajat, migraatio vain skeeman omistavassa projektissa. |
