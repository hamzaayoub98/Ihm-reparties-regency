<template>
    <div id="home" >
        <Asteroid  v-show="isShow"></Asteroid>
        <AsteroidLeft v-show="isShow"></AsteroidLeft>
        <div class="box">
            <ToggleButton />
        </div>
        <button id="b1" v-on:click="action(1)">
            <img  id="button1"  src="../assets/blue_button.png">
        </button>
        <VueSlider v-model="sliderValue" id="slider" v-on:change="sendSliderValue"/>
        <button id="b2" v-on:click="action(2)">
            <img  id="button2"  src="../assets/redButton.png">
        </button>
        <button v-on:click="sendPing()">
            <img  id="button3"  src="../assets/send.png">
        </button>
        <button @click="isShow = !isShow" id="asteroidsVue" v-on:click="action(8)" >Fire !</button>
    </div>
</template>

<script>
    import  Axios from 'axios';

    import Asteroid from "./Asteroid";
    import { URL_REST, URL_WS } from '../main.js'
    import VueSlider from 'vue-slider-component'
    import 'vue-slider-component/theme/antd.css'
    import AsteroidLeft from './AsteroidLeft';
    import ToggleButton from './ToggleButton.vue';

    export default {
        name: "Home",
        components: {Asteroid,VueSlider,AsteroidLeft,ToggleButton},
        data(){
            return {
                info : null,
                photoSrc:["../assets/blue_button.png","../assets/kc.png"],
                mySrc:0,
                connection: null,
                sliderValue:0,
                isShow: true,
                finished : false,
                doc:null,
                button1:null,
                button2:null,

            }
        },
        props: {
            msg: String,
        },
        created: function() {
            this.initWSConnection();
        },
        mounted() {
          let button1Pressed = false;
          let button2Pressed = false;
            this.doc = document.getElementById("home")
            this.button1 = document.getElementById("b1")
            this.button2 = document.getElementById("b2")
            Axios.get("http://" + URL_REST)
            .then(response =>(this.info = response,
            console.log(response)));
          this.button1.addEventListener('touchstart',function (event){
                console.log("b1",event);
                button1Pressed = true;
            if(button1Pressed && button2Pressed){
              console.log("multi touch detected")
              Axios.post("http://" + URL_REST + "/action", {
                action: 7,
              })
                  .then(res => {
                    console.log(`statusCode: ${res.status}`)
                    console.log(res)
                  })
                  .catch(error => {
                    console.log(error);
                  });
            }
          });
          this.button2.addEventListener('touchstart',function (event){
                console.log("b2",event);
                button2Pressed = true;
              console.log(button1Pressed, button2Pressed)
            if(button1Pressed && button2Pressed){
              console.log("multi touch detected")
              Axios.post("http://" + URL_REST + "/action", {
                action: 7,
              })
                  .then(res => {
                    console.log(`statusCode: ${res.status}`)
                    console.log(res)
                  })
                  .catch(error => {
                    console.log(error);
                  });
            }
          });
          this.button1.addEventListener('touchend',function (event){
            console.log("b1-end",event);
            this.button1Pressed = false;
          });
          this.button2.addEventListener('touchend',function (event){
            console.log("b2-end",event);
            this.button2Pressed = false;
          });
        },
        methods: {
            action:function (number) {
                Axios.post("http://" + URL_REST + "/action", {
                    action: number,
                })
                    .then(res => {
                        console.log(`statusCode: ${res.status}`)
                        console.log(res)
                    })
                    .catch(error => {
                        console.log(error);
            });
            },
            initWSConnection: function() {
                console.log("Starting connection to WebSocket Server")
                this.connection = new WebSocket("ws://" + URL_WS + "?id=0")
                this.connection.onmessage = function(event) {
                    console.log(event.data);
                }
                this.connection.onopen = function(event) {
                    console.log(event);
                    console.log("Successfully connected to the websocket server...")
                }
            },
            sendPing: function() {
                this.connection.send('PING')

            },
            sendSliderValue:function(){
              this.connection.send(['sliderValue',this.sliderValue]);
            },
            sendAsteroidsState:function(){
              this.connection.send(['AsteroidsState',this.isShow]);
            },
            concurentTouch:function (){
              console.log("ok")
              if(this.button1Pressed && this.button2Pressed){
                console.log("multitouch detected")
                Axios.post("http://" + URL_REST + "/action", {
                  action: 7,
                })
                    .then(res => {
                      console.log(`statusCode: ${res.status}`)
                      console.log(res)
                    })
                    .catch(error => {
                      console.log(error);
                    });
              }
            }
        },
    }
</script>

<style scoped>
    #slider{

      position: relative;
      top:900px;
      display: flex;
      margin: 50 auto;
    }
    #button1{
        position: absolute;
        top:550px;
        right: 485px;
        height: 6%;
        width: 7%;
        border-radius: 10%;
    }
    #button2{
        position: absolute;
        top:550px;
        left: 485px;
        height: 6%;
        width: 7%;
        border-radius: 10%;
    }
    #button3{
        position: absolute;
        top:65%;
        left: 200px;
        height: 5%;
        width: 5%;
        border-radius: 10%;
    }
    #home{
        width: 100%;
        height: 100%;
        position: absolute;
    }
    #box {
    text-align:center;
    margin-bottom: 30px;
    }





</style>
