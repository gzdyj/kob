<template>
  <div class="window">
    <div class="game-body">
      <MenuView v-if="$store.state.router.router_name==='menu'"/>
      <PkindexViewVue v-else-if="$store.state.router.router_name==='pk'"/>
      <RecordIndexViewVue v-else-if="$store.state.router.router_name==='record'"/>
      <RecordContentViewVue v-else-if="$store.state.router.router_name==='record_content'"/>
      <RanklistIndexViewVue v-else-if="$store.state.router.router_name==='ranklist'"/>
      <UserBotIndexViewVue v-else-if="$store.state.router.router_name==='user_bot'"/>
       
    </div>
  </div>



</template>


<script>

import { useStore } from 'vuex'
import MenuView from './views/MenuView.vue'
import PkindexViewVue from "./views/pk/PKindexView.vue"
import RecordIndexViewVue from "./views/record/RecordIndexView.vue"
import RecordContentViewVue from "./views/record/RecordContentView.vue"
import RanklistIndexViewVue from "./views/ranklist/RanklistIndexView.vue"
import UserBotIndexViewVue from "./views/user/bot/UserBotIndexView.vue"


export default {
  components: {
    MenuView,
    PkindexViewVue,
    RecordIndexViewVue,
    RanklistIndexViewVue,
    RecordContentViewVue,
    UserBotIndexViewVue,

},
  setup() {
    const store = useStore();
    const jwt_token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI5MDJiNGY3NGNhYjc0MTI0ODc5NTgxNjRjNzZmYWYzOCIsInN1YiI6IjEiLCJpc3MiOiJzZyIsImlhdCI6MTY2MjE3MTA1NSwiZXhwIjoxNjYzMzgwNjU1fQ.EpYpxUAab6l6rZxardZ0K_gD-S3OvRrMBsANvA-_W3o";
        if (jwt_token) {
            store.commit("updateToken", jwt_token);
            store.dispatch("getinfo", {
                success() {
                    store.commit("updataPullinginfo", false);
                },
                error() {
                    store.commit("updataPullinginfo", false);
                }
            })
        } else {
            store.commit("updataPullinginfo", false);
        }
  }

}

</script>

<style scoped>
body {
  margin: 0;
}

div.game-body {
  background-image: url(https://cos.zinzin.cc//images_1/20220830180219.png?imageMogr2/format/webp);
  /* 背景图垂直、水平均居中 */
  background-position: center center;
  /* 背景图不平铺 */
  background-repeat: no-repeat;
  /* 当内容高度大于图片高度时，背景图像的位置相对于viewport固定 */
  background-attachment: fixed;
  /* 让背景图基于容器大小伸缩 */
  background-size: cover;
  /* 设置背景颜色，背景图加载过程中会显示背景色 */
  background-color: #464646;

  /* 宽高百分之百 */
  width: 100%;
  height: 100%;
}

div.window {
  width: 100vw;
  height: 100vh;
}
</style>
