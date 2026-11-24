# Razhroščevanje aplikacije AndroidStudioDebugger

**Predmet:** Razvoj mobilnih aplikacij  
**Datum:** 24. 11. 2026

## 1. Pregled aplikacije

V tej nalogi sem pregledal aplikacijo AndroidStudioDebugger. To je preprost kalkulator, v katerega vnesemo dve števili in izberemo eno od štirih operacij: ADD, SUB, DIV ali MUL.

Pri pregledu sem ugotovil, da so gumbi in vnosna polja določeni v datoteki `activity_main.xml`. Glavna koda je v datoteki `MainActivity.java`. Projekt je malo drugačen od primera SimpleCalc v navodilih: nima ločenega razreda `Calculator` in metod `onAdd()`, `onSub()`, `onDiv()` ter `onMul()`. Klik na gumb pokliče skupno metodo `calculate()`, ki glede na izbrano operacijo izvede račun. Vnos pretvori metoda `readNumber()`.

Za računanje aplikacija uporablja tip `BigDecimal`. Pred pretvorbo zamenja decimalno vejico s piko, zato lahko uporabnik vnese decimalno število z vejico ali piko.

## 2. Preverjanje računskih operacij

Na podlagi kode sem ugotovil naslednje pričakovane rezultate:

| Vnos | Operacija | Rezultat oziroma odziv |
|---|---|---|
| 55 in 25 | ADD | 80 |
| 55 in 25 | SUB | 30 |
| 55 in 25 | DIV | 2.2 |
| 55 in 25 | MUL | 1375 |
| 55 in 0 | DIV | Opozorilo, da deljenje z ničlo ni dovoljeno |
| Prazno polje | Katerakoli operacija | Opozorilo, da je treba vnesti veljavno število |

Besedilo iz vnosnega polja se pretvori v `BigDecimal` v metodi `readNumber()`. Če je polje prazno ali vsebuje neveljavno vrednost, pretvorba sproži izjemo `NumberFormatException`. Program jo prestreže, označi napačno vnosno polje in prekine izračun. Zato se aplikacija zaradi praznega polja ne sesuje.

## 3. Prekinitvene točke in izvajanje po korakih

Za prvo prekinitveno točko bi izbral vrstico `BigDecimal a = readNumber(firstNumber);` v metodi `calculate()`. Tu se program ustavi, preden prebere prvo število. Za drugo točko bi izbral vrstico `switch (operation)`, kjer sta števili že prebrani.

Imena spremenljivk so v tem projektu drugačna kot v učnem listu. Namesto `operandOne`, `operandTwo` in `operator` se uporabljajo `a`, `b` in `operation`. Pri vnosu 55 in 25 bi zato pričakoval vrednosti `a = 55`, `b = 25` in `operation = '+'`.

| Ukaz | Kaj naredi |
|---|---|
| Step Over (F8) | Izvede trenutno vrstico, vendar ne vstopi v poklicano metodo. |
| Step Into (F7) | Vstopi v poklicano metodo, na primer v `readNumber()`. |
| Step Out (Shift+F8) | Dokonča trenutno metodo in se vrne v metodo, ki jo je poklicala. |
| Resume (F9) | Nadaljuje izvajanje do naslednje prekinitvene točke ali konca programa. |

Klicni sklad oziroma Frames pokaže, katera metoda je poklicala trenutno metodo. Tako bi lahko sledil poti od klika na gumb do metode `calculate()` in naprej do `readNumber()`.

Za pogojno prekinitveno točko pri številu 42 je treba upoštevati, da sta `a` in `b` tipa `BigDecimal`. Uporabil bi pogoj:

```java
(a.compareTo(BigDecimal.valueOf(42)) == 0) || (b.compareTo(BigDecimal.valueOf(42)) == 0)
```

Pogoj bi se sprožil pri vnosih 42 in 7 ter 7 in 42. Pri vnosih 12 in 7 ali 7 in 8 se ne bi sprožil.

## 4. Spremenljivke in Evaluate Expression

Če bi med razhroščevanjem pri seštevanju 42 in 7 spremenil `a` in `b` na `new BigDecimal("10")`, bi program izračunal 20. Besedilo v vnosnih poljih bi še vedno ostalo 42 in 7, ker sprememba spremenljivk v razhroščevalniku ne spremeni vsebine polj na zaslonu.

Podobno bi pri množenju s spremembo obeh spremenljivk na `new BigDecimal("3")` dobil rezultat 9.

Pri prvotnem vnosu 55 in 25 bi izrazi vrnili:

| Izraz | Rezultat |
|---|---|
| `a.add(b)` | 80 |
| `a.multiply(b)` | 1375 |
| `a.compareTo(BigDecimal.valueOf(42)) == 0` | `false` |
| `firstNumber.getHint()` | `Prvo stevilo` |
| `firstNumber.getText().toString()` | Vpisano besedilo, na primer `55` |

Pri Evaluate Expression moram biti previden pri klicih metod, ker lahko nekatere metode spremenijo stanje programa. Varneje je najprej uporabiti izraze, ki samo preverijo ali izračunajo vrednost.

## 5. Prazni vnosi in deljenje z ničlo

V kodi je prazno ali napačno vnosno polje že obravnavano. Metoda `readNumber()` prestreže `NumberFormatException`, prikaže sporočilo »Vnesi veljavno stevilo.« in ne nadaljuje z izračunom. Če sta prazni obe polji, program najprej označi prvo polje in se vrne.

Pred deljenjem program preveri, ali je drugo število enako nič. To velja tudi za račun 0 / 0. V tem primeru se izpiše opozorilo »Deljenje z niclo ni dovoljeno.«. Pri tipu `BigDecimal` bi deljenje z ničlo sicer sprožilo `ArithmeticException`; program ga prepreči s preverjanjem pred deljenjem. Zato tukaj ne dobimo `Infinity` ali `NaN`, kot se lahko zgodi pri tipu `double`.

Pri deljenju 1.6753456 z 0.1 je pričakovani rezultat 16.753456. Aplikacija pri deljenju uporabi `MathContext.DECIMAL64`, ki določa natančnost rezultata.

| Preizkus | Pričakovano vedenje glede na kodo |
|---|---|
| Prazno prvo polje in 25 v drugem | Opozorilo pri prvem polju; izračun se ustavi. |
| 55 v prvem in prazno drugo polje | Opozorilo pri drugem polju; izračun se ustavi. |
| Obe polji prazni | Opozorilo pri prvem polju. |
| 55 / 0 ali 0 / 0 | Opozorilo, da deljenje z ničlo ni dovoljeno. |
| 1.6753456 / 0.1 | 16.753456 |

## 6. Odgovori na vprašanja

1. **Kakšna je razlika med Run in Debug?** Run aplikacijo običajno zažene. Debug omogoča ustavljanje na prekinitvenih točkah in pregled izvajanja.
2. **Kaj je prekinitvena točka?** To je mesto v kodi, kjer se izvajanje začasno ustavi. Nastavim jo s klikom ob številki vrstice.
3. **Kdaj uporabim Step Into in kdaj Step Over?** Step Into uporabim, ko želim vstopiti v klicano metodo. Step Over izvede vrstico brez vstopa v metodo.
4. **Kaj prikazuje Frames?** Prikazuje trenutne metode in klicno pot do mesta, kjer se program ustavi.
5. **Kaj prikazujeta Variables in Watches?** Variables pokaže trenutne spremenljivke. Watches spremlja izraze, ki jih izberem.
6. **Kaj pomeni pogoj za vrednost 42?** Prekinitvena točka se sproži, če je vsaj eno od obeh števil enako 42.
7. **Kako spremenim vrednost spremenljivke med razhroščevanjem?** V oknu Variables izberem spremenljivko in nastavim novo vrednost.
8. **Čemu služi Evaluate Expression?** Z njim preverim vrednost izraza v trenutnem delu programa. Pri klicih metod moram paziti na stranske učinke.
9. **Zakaj je prazen niz težava?** Praznega niza ni mogoče pretvoriti v število, zato nastane `NumberFormatException`.
10. **Kako se razlikuje deljenje z ničlo pri `double` in `BigDecimal`?** `double` lahko vrne `Infinity` ali `NaN`, `BigDecimal` pa sproži `ArithmeticException`. V tej aplikaciji se deljenje ustavi že s predhodnim preverjanjem.

## 7. Dokazila

V mapi `docs/screenshots/` sta že posnetka aplikacije `calculator.png` in `addition.png`. Ne prikazujeta pa okna Debuggerja. Za oddajo moram v Android Studiu zajeti še naslednja dokazila:

1. **Debug in Frames:** ustavitev v `calculate()` ter prikaz spremenljivke `operation`.
2. **Step Into in klicni sklad:** ustavitev v `readNumber()` in prikaz klicne poti.
3. **Pogojna prekinitvena točka:** nastavljen pogoj za število 42 ter preizkus z 42 in brez njega.
4. **Sprememba spremenljivk:** spremenjena `a` in `b` v Variables ter prikazan rezultat.
5. **Prazen vnos:** ustavitev tik pred `new BigDecimal(text)` in pregled praznega niza. Izjema je prestrežena, zato se aplikacija ne sesuje.
6. **Robni primeri:** opozorilo pri deljenju z ničlo in rezultat decimalnega preizkusa.

## 8. Zaključek

Pri pregledu kode sem ugotovil, da kalkulator izvaja štiri osnovne operacije in že vsebuje preverjanje praznih oziroma napačnih vnosov ter deljenja z ničlo. Posebnega popravka za ta primera zato ni treba dodati.

Rezultati v tem poročilu so izpeljani iz izvorne kode. Dejanske seje Android Studio Debug in posnetkov okna Debuggerja še nisem zajel, zato jih moram dodati po izvedbi razhroščevanja.
