while true
do
  git add .
  git commit -m "auto-backup"
  git push
  sleep 300
done
