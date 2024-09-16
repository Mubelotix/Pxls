scp -r mubelotix@insagenda.insa.lol:/home/mubelotix/insaplace-backup ./backup
docker compose up -d
pip install pipenv
pipenv install
pipenv run python database/db2pixellogs.py pxls -f pixels.log -p 15432
rm pixels.gif
pipenv run python logs/timelapse.py pixels.log --config-path pxls.conf --output-path pixels.gif --every 35 --scale 5
