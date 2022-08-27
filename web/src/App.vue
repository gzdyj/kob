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
