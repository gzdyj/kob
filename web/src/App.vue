<template>
  <div id="nav">
      <div>Bot名称：{{ bot_name }}</div>
      <div>Bot战力：{{ bot_rating }}</div>

  </div>
  <router-view/>
</template>


<script>

import $ from 'jquery';
import { ref } from 'vue';

export default {
  name: "App",
  setup: () => {
    let bot_name = ref("");
    let bot_rating = ref("");

    $.ajax({
      url: "http://127.0.0.1:3000/pk/getbotinfo/",
      type: "get",
      success: resp => {
        bot_name.value = resp.name;
        bot_rating.value = resp.bot_rating;
      }
    });

    return {
      bot_name,
      bot_rating,
    }

  }

}

</script>

<style>
body {

  background-image: url("C:/Users/14700/Desktop/sping/kob/web/src/assets/background.png");
  background-size: cover;

}
</style>
