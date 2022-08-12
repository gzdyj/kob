<template>

    <PlayGround v-if="$store.state.pk.status === 'playing'" />
    <MatchGround v-if="$store.state.pk.status === 'matching'" />

</template>


<script>
//引入匹配界面
import MatchGround from '../../components/MatchGround.vue'
import PlayGround from '../../components/PlayGround.vue'
//onMounted 安装 挂载
import { onMounted, onUnmounted } from 'vue'
import { useStore } from 'vuex'


export default {
    components: {
        PlayGround,
        MatchGround,
    },
    setup() {
        const store = useStore();
        //对滴，字符串中有${}表达式操作的话，需要用`，不能用引号
        const socketUrl = `ws://127.0.0.1:3000/websocket/${store.state.user.token}/`;

        let socket = null;

        onMounted(() => {
            //提前设置一下未匹配的时候对手的头像和名称（默认）
            store.commit("updateOpponent", {
                username: "我的对手",
                photo: "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png",
            })

            //js自带类WebSocket
            socket = new WebSocket(socketUrl);

            socket.onopen = () => {
                console.log("connected!");
                store.commit("updateSocket", socket);
            }

            socket.onmessage = msg => {
                const data = JSON.parse(msg.data);
                if (data.event === "start-matching") { //匹配成功
                    store.commit("updateOpponent", {
                        username: data.opponent_username,
                        photo: data.opponent_photo,
                    });
                    //匹配成功2秒后切换游戏界面
                    setTimeout(() => {
                        store.commit("updateStatus", "playing");
                    }, 2000);
                    //接收地图
                    store.commit("updateGamemap", data.gamemap);

                }
            }

            socket.onclose = () => {
                console.log("disconnected!");
            }
        })



        //每次卸载的时候一定要建立一个新的连接，否则切换页面，连接不会断开，造成冗余
        onUnmounted(() => {
            socket.close();
            store.commit("updateStatus", "matching");
        })

    }

}
</script>
<style scoped>
</style>