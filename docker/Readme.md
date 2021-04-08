1. Clone repo and `cd [repo-root]/docker`
3. Build docker image ` docker build -t fishing . `
4. While this builds, download stuff (here only German (de) is downloaded, and the kb file seems to be mandatory):
 `cd [repo-root]/data/db`
 `wget https://science-miner.s3.amazonaws.com/entity-fishing/0.0.3/linux/db-de.zip `
 `wget https://science-miner.s3.amazonaws.com/entity-fishing/0.0.3/linux/db-en.zip `
 `wget https://science-miner.s3.amazonaws.com/entity-fishing/0.0.3/linux/db-kb.zip `
 `unzip db-de.zip`
 `unzip db-kb.zip`
 `mv *zip ..`
5. Run container:
 ` cd [repo-root]`
 ` docker run --rm --name fishing_ctr -p 8090:8090 -v ${PWD}/data/db:/fishing/nerd/data/db/ -it fishing`
6. Enjoy in `http://localhost:9897/`
  Example: `curl 'http://localhost:9897/service/disambiguate' -X POST -F "query={ 'text': 'Die Schlacht bei Tannenberg war eine Schlacht des Ersten Weltkrieges und fand in der Gegend südlich von Allenstein in Ostpreußen vom 26. August bis 30. August 1914 zwischen deutschen und russischen Armeen statt. Die deutsche Seite stellte hierbei 153.000 Mann, die russische Seite 191.000 Soldaten ins Feld. Sie endete mit einem Sieg der deutschen Truppen und der Zerschlagung der ins südliche Ostpreußen eingedrungenen russischen Kräfte.', 'language':'de'}"`
