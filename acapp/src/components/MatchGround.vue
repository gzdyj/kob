<template>
    <div class="matchground-field">
        <div class="matchground">
            <div class="matchground-head">
                <div>
                    <div class="user-photo">
                        <img :src="$store.state.user.photo" alt="">
                    </div>
                    <div class="user-username">
                        {{ $store.state.user.username }}
                    </div>
                </div>

                <div>
                    <div class="user-select-bot">
                    <select v-model="select_bot" class="form-select" aria-label="Default select example">
                        <option value="-1" selected>御驾亲征</option>
                        <option v-for="bot in bots" :key="bot.id" :value="bot.id">
                            {{ bot.title }}
                        </option>
                    </select>
                </div>

                </div>


                
                <div>
                    <div class="user-photo">
                        <img :src="$store.state.pk.opponent_photo" alt="">
                    </div>
                    <div class="user-username">
                        {{ $store.state.pk.opponent_username }}
                    </div>
                </div>
            </div>
            <div class="start-match-button">
                <button @click="click_match_btn" type="button" class="btn btn-primary btn-lg">{{ match_btn_info
                }}</button>
            </div>
        </div>
    </div>


</template>

<script>
import { ref } from 'vue'
import store from '../store';
import $ from 'jquery';

export default {
    setup() {
        let match_btn_info = ref("开始匹配");
        let bots = ref([]);
        let select_bot = ref("-1");

        const click_match_btn = () => {
            if (match_btn_info.value === "开始匹配") {
                match_btn_info.value = "取消";
                store.state.pk.socket.send(JSON.stringify({
                    event: "start-matching",
                    bot_id: select_bot.value,
                }))
            } else {
                match_btn_info.value = "开始匹配";
                store.state.pk.socket.send(JSON.stringify({
                    event: "stop-matching",
                }))

            }
        }

        const refresh_bots = () => {
            $.ajax({
                url: "https://snake.zinzin.cc/api/user/bot/getlist/",
                type: "GET",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    bots.value = resp;
                }
            })
        }

        refresh_bots(); //从云端动态获取bot

        return {
            match_btn_info,
            click_match_btn,
            bots,
            select_bot,
        }
    }
}

</script>

<style scoped>

div.matchground-field{
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;
}    
div.matchground {
    width: 60%;
    height: 60%;
    background-color: rgba(50, 50, 50, 0.5);
    display: flex;
    flex-direction: column;
    justify-content: space-around;
}
div.matchground-head {
    display: flex;
    justify-content: space-evenly;
}
div.user-photo {
    text-align: center;
    /* 上边距 */
    padding-top: 5vh;
}

div.user-photo>img {
    border-radius: 50%;
    width: 10vw;
    height: 50%;
}

div.user-username {
    text-align: center;
    font-size: 20px;
    font-weight: 600;
    color: white;
    padding-top: 2vh;
}

div.user-select-bot {
    padding-top: 30vh;
    width: 15vw;
    text-align: center;
}

div.user-select-bot > select {
    margin: 0 auto;
    width: 10vw;
    font-size: 20px;
    border-radius: 5px;
    height: 4.5vh;

}
.start-match-button {
    text-align: center;

}
.start-match-button > button {
    font-size: 20px;
    border-radius: 5px;
    background-color: #0D6EFD;
    padding: 5px 10px;
    border: none;
    cursor: pointer;
}
</style>