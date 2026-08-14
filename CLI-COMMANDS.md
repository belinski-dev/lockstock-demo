## Run

```bash
./mvnw package -DskipTests
docker-compose up --build -d
```

## Logging

### just logs
```bash
unbuffer docker-compose logs -f | grep --color=never 'lockstock-app'
```

### count duplicates
```bash
unbuffer docker-compose logs | grep picked | awk '{print $NF}' | sort | uniq -cd | sort -rn | head
```

## Rebuild / Restart

### cleanup + image rebuild
```bash
docker-compose down -v && docker-compose up --build
```

### same + app rebuild
```bash
docker-compose down -v && ./mvnw package -DskipTests && docker-compose up --build -d
```