# BallGame_Cloud_Project
# BALL GAME - Gra z Funkcjonalnościami Chmurowymi

## Opis Projektu

Projekt "BALL GAME" to aplikacja stworzona w języku Java, której celem jest integracja gry z funkcjami chmurowymi. Gra polega na kontrolowaniu platformy, która odbija piłkę o zmiennej prędkości. Po zakończeniu rozgrywki, wyniki są przesyłane do chmury, przechowywane w bazie danych oraz wyświetlane na stronie internetowej.

### Kluczowe funkcje:
- **Logowanie użytkownika** – Z wykorzystaniem unikalnego ID i nicku.
- **Przesyłanie wyników do maszyny wirtualnej w chmurze (AWS EC2)**.
- **Wizualizacja wyników w czasie rzeczywistym** na stronie internetowej (AWS S3).
- **Przechowywanie wyników w bazie danych RDS**.
- **Automatyczne tworzenie kopii zapasowych** przy użyciu AWS Backup.

## Technologie

- **Język Programowania**: Java (JDK 11 lub wyższy).
- **AWS EC2**: Maszyna wirtualna do przechowywania wyników gry.
- **AWS RDS**: Baza danych do przechowywania wyników gry.
- **AWS S3**: Strona internetowa, na której wyświetlane są wyniki w czasie rzeczywistym.
- **AWS Backup**: Automatyczne kopie zapasowe bazy danych RDS.

## Architektura Systemu

Aplikacja składa się z kilku komponentów:
- **Gra w Java**: Odpowiedzialna za logowanie, rozgrywkę i generowanie wyników.
- **AWS EC2**: Maszyna wirtualna przechowująca dane.
- **AWS RDS**: Baza danych do długoterminowego przechowywania wyników.
- **AWS S3**: Strona internetowa wyświetlająca wyniki w czasie rzeczywistym.
- **AWS Backup**: Automatyczne kopie zapasowe danych.
