<template>
  <NavBar />
  <router-view />
</template>


<script>

import NavBar from './components/NavBar.vue'
import "bootstrap/dist/css/bootstrap.min.css"
import "bootstrap/dist/js/bootstrap"


export default {
  components: {
    NavBar
  },
  //   setup() {




  //     $.ajax({
  //       url: "http://127.0.0.1:3000/user/account/register/",
  //       type: "post",
  //       data: {
  //         username: "gz111",
  //         password: "123",
  //         confirmedPassword: "12113",
  //       },
  //       success(resp) {
  //         console.log(resp);
  //       },
  //       error(resp) {
  //         console.log(resp);
  //       }
  //     });
  //   }
}

</script>

<style>
body {

  background-image: url("./assets/images/background1.jpg");
  /* background-position: center center;
  background-size: cover;
  background-repeat: no-repeat;
  width: 100vw; 
  height: 100vw; */
  width: 100vw;
  height: 100vw;

  background-size: cover;


}
</style>
