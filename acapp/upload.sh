scp dist/js/*.js myserver2:/data/wwwroot/app3277.acapp.acwing.com.cn/acapp
scp dist/css/*.css myserver2:/data/wwwroot/app3277.acapp.acwing.com.cn/acapp

ssh myserver2 'cd /data/wwwroot/app3277.acapp.acwing.com.cn/acapp && ./rename.sh'
