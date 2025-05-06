***1.  Обновление пакетов***

```
sudo apt-get update  
```

***2. Установка GlusterFS***

```
sudo apt-get install -y glusterfs-server attr
```

***3. Запускаем гластер, устанавливаем в атозагрузку и проверяем статус:***

```
sudo systemctl start glusterd
sudo systemctl enable glusterd
```

```
sudo systemctl status glusterd
```

***4. Настройка trusted pool***

На одной из нод
```
sudo gluster peer probe 192.168.49.3
sudo gluster peer probe 192.168.49.4
```

Можно проверить статус пиров

```
sudo gluster peer status
```

***5. Создание тома с репликацией***

На всех нодах создайте директорию для данных:

```
sudo mkdir -p /data/gfsvolume
```

***6. На одной из нод создать volume***

```
sudo gluster volume create gfsvol replica 3  \
192.168.49.2:/data/gfsvolume \
192.168.49.3:/data/gfsvolume  \
192.168.49.4:/data/gfsvolume 
```

Запустите том и проверьте его статус

```
sudo gluster volume start gfsvol

sudo gluster volume status gfsvol
sudo gluster volume info gfsvol
```

***7. На каждой ноде смонтируйте том***

```
sudo mkdir -p /mnt/nfs-storage/k8s
sudo mount -t glusterfs localhost:/gfsvol /mnt/nfs-storage/k8s
```

***8 Проверка репликации***

```
sudo touch /mnt/nfs-storage/k8s/test_replication_file
echo "This is a GlusterFS replication test" | sudo tee /mnt/nfs-storage/k8s/test_replication_file
```
***9.  Настройка автомонтирования, при перезапуске ноды***

```
sudo bash -c 'cat > /etc/systemd/system/glusterfs-mount.service <<EOF
[Unit]
Description=Mount GlusterFS Volume
After=network.target
BindsTo=glusterd.service

[Service]
Type=oneshot
RemainAfterExit=yes
ExecStart=/bin/sh -c "until /usr/sbin/gluster volume status gfsvol >/dev/null 2>&1; do sleep 1; done; /bin/mount -t glusterfs localhost:/gfsvol /mnt/nfs-storage/k8s"
TimeoutSec=0

[Install]
WantedBy=multi-user.target
EOF'
```

Активируем сервис

```
sudo systemctl daemon-reload
sudo systemctl enable --now glusterfs-mount.service
```
