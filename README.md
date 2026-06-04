# Budget REST API

## Wymagania

Przed uruchomieniem aplikacji upewnij się, że masz zainstalowane i włączone narzędzie:
* **Docker** wraz z obsługą `docker compose`

---

## Uruchomienie aplikacji

Wszystkie zasoby (baza danych PostgreSQL oraz aplikacja Spring Boot) konfigurują się i uruchamiają automatycznie wewnątrz kontenerów.

1. Otwórz terminal w głównym folderze projektu (tam, gdzie znajduje się plik `docker-compose.yml`).
2. Uruchom poniższą komendę:

   ```bash
   docker-compose up --build
## Uruchamianie Testow
Aby uruchomić testy jednostkowei integracyjne, możesz użyć następującej komendy:

    docker compose run --rm app-tests
## Interfejs API (Swagger)

Gdy kontenery pomyślnie wystartują, pełna dokumentacja wszystkich endpointów oraz interfejs do wysyłania testowych żądań (Swagger UI) są dostępne pod adresem:

**[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**