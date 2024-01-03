Build
```
docker build . -t webcrawler/backend
```
Run
```
docker run -e BASE_URL=ANY_URL -p 4567:4567 --rm webcrawler/backend
```