<template>
    <div id="home" >
        <Asteroid  v-show="isShow"></Asteroid>
        <AsteroidLeft v-show="isShow"></AsteroidLeft>
        <div class="box">
            <ToggleButton />
        </div>
        <button id="b1" :disabled="buttonVisible===false" v-on:click="action(1)">
            <img  id="button1" v-bind:class="buttonVisible?'button1':'button1Disabled'"  src="../assets/blue_button.png">
        </button>

        <VueSlider v-model="sliderValue3" id="sliderBar" v-on:change="sendSliderValue3"/>

        <button id="b2" :disabled="buttonVisible===false" v-on:click="action(2)">
            <img  id="button2"  v-bind:class="buttonVisible?'button2':'button2Disabled'"     src="../assets/redButton.png">
        </button>
        <button id="b5" :disabled="buttonVisible===false" v-on:click="action(9)">
            <img  id="button5"  v-bind:class="buttonVisible?'button5':'button5Disabled'"     src="../assets/orange.png">
        </button>
        <button id="b6" :disabled="buttonVisible===false" v-on:click="action(9)">
            <img  id="button6"  v-bind:class="buttonVisible?'button6':'button6Disabled'"     src="../assets/orange.png">
        </button>
        <button id="b7" :disabled="buttonVisible===false" v-on:click="action(9)">
            <img  id="button7"  v-bind:class="buttonVisible?'button7':'button7Disabled'"     src="../assets/orange.png">
        </button>
        <button id="b8" :disabled="buttonVisible===false" v-on:click="action(9)">
            <img  id="button8"  v-bind:class="buttonVisible?'button8':'button8Disabled'"     src="../assets/orange.png">
        </button>



        <button v-on:click="sendPing()">
            <img  id="button3"  src="../assets/send.png">
        </button>
        <button @click="isShow = !isShow" id="asteroidsVue" v-on:click="action(8)" >
          fire
        </button>
        <b-button id="b4" :disabled="buttonVisible===false" size="lg" variant="primary" >Action</b-button>

        <button id="b10" :disabled="buttonVisible === false" v-on:click="activateAntiMater">
            <img id="button10" src="../assets/energy.png" v-bind:class="buttonVisible?'button10':'button10Disabled'">
        </button>
        <round-slider  v-bind:update="sendSliderValue"
        v-bind:change="mouseDown"
          id="roundslider"
          v-model="sliderValue"
          start-angle="315"
          end-angle="+270"
          line-cap="round"
          radius="120"
          rangeColor="red"
        />

        <round-slider   v-bind:update="sendSliderValue2"
        v-bind:change="mouseDown2"
          id="roundslider2"
          v-model="sliderValue2"
          start-angle="315"
          end-angle="+270"
          line-cap="round"
          radius="120"
          rangeColor="red"
        />
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
    import RoundSlider from "vue-round-slider";

    export default {
        name: "Home",
        components: {Asteroid,AsteroidLeft,ToggleButton,RoundSlider,VueSlider},
        data(){
            return {
                info : null,
                photoSrc:["../assets/blue_button.png","../assets/kc.png"],
                mySrc:0,
                connection: null,
                sliderValue:0,
                sliderValue2:0,
                onSlider :false,
                isShow: true,
                finished : false,
                doc:null,
                button1:null,
                button2:null,
                buttonVisible:false,
                onSlider2:false,
                sliderValue3:0,
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

          let button5Pressed = false;
          let button6Pressed = false;
          let button7Pressed = false;
          let button8Pressed = false;

            this.doc = document.getElementById("home")
            this.button1 = document.getElementById("b1")
            this.button2 = document.getElementById("b2")
            this.button5 = document.getElementById("b5")
            this.button6 = document.getElementById("b6")
            this.button7 = document.getElementById("b7")
            this.button8 = document.getElementById("b8")
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

          this.button5.addEventListener('touchstart',function (event){
                console.log("b5",event);
                button5Pressed = true;
              console.log(button5Pressed, button6Pressed,button7Pressed, button8Pressed)
            if(button5Pressed && button6Pressed && button7Pressed && button8Pressed){
              console.log("multi touch detected")
              Axios.post("http://" + URL_REST + "/action", {
                action: 9,
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
          this.button6.addEventListener('touchstart',function (event){
                console.log("b6",event);
                button6Pressed = true;
              console.log(button5Pressed, button6Pressed,button7Pressed, button8Pressed)
            if(button5Pressed && button6Pressed && button7Pressed && button8Pressed){
              console.log("multi touch detected")
              Axios.post("http://" + URL_REST + "/action", {
                action: 9,
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
          this.button7.addEventListener('touchstart',function (event){
                console.log("b7",event);
                button5Pressed = true;
              console.log(button5Pressed, button6Pressed,button7Pressed, button8Pressed)
            if(button5Pressed && button6Pressed && button7Pressed && button8Pressed){
              console.log("multi touch detected")
              Axios.post("http://" + URL_REST + "/action", {
                action: 9,
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
          this.button8.addEventListener('touchstart',function (event){
                console.log("b5",event);
                button8Pressed = true;
              console.log(button5Pressed, button6Pressed,button7Pressed, button8Pressed)
            if(button5Pressed && button6Pressed && button7Pressed && button8Pressed){
              console.log("multi touch detected")
              Axios.post("http://" + URL_REST + "/action", {
                action: 9,
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

          this.button5.addEventListener('touchend',function (event){
            console.log("b5-end",event);
            this.button5Pressed = false;
          });
          this.button6.addEventListener('touchend',function (event){
            console.log("b6-end",event);
            this.button6Pressed = false;
          });
          this.button7.addEventListener('touchend',function (event){
            console.log("b7-end",event);
            this.button7Pressed = false;
          });
          this.button8.addEventListener('touchend',function (event){
            console.log("b5-end",event);
            this.button8Pressed = false;
          });
            window.setInterval(() => {
                this.checkButtonState();
            }, 500)
          window.setInterval(() => {
            if(!this.onSlider){
              //this.sliderValue--;
            }
          },100)
          window.setInterval(() => {
            if(!this.onSlider2){
              //this.sliderValue2--;
            }
          },100)
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
                    console.log("message",event.data);
                }
                this.connection.onopen = function(event) {
                    console.log(event);
                    console.log("Successfully connected to the websocket server...")
                }
            },
            sendPing: function() {
                this.connection.send('PING')

            },
            mouseDown:function() {
              this.onSlider=true
              this.connection.send(['sliderValue',this.sliderValue]);
                 /*var _this = this;
                 _this.sliderValue=0;*/
              this.onSlider=false
            },
            mouseDown2:function() {
              this.onSlider2=true
              this.connection.send(['sliderValue2',this.sliderValue2]);
                 /*var _this = this;
                 _this.sliderValue2=0;*/
              this.onSlider2=false

            },
            sendSliderValue:function(){
              this.connection.send(['sliderValue',this.sliderValue]);
            },
            sendSliderValue2:function(){
              this.connection.send(['sliderValue2',this.sliderValue2]);
            },
            sendSliderValue3:function(){
              this.connection.send(['sliderValue3',this.sliderValue3]);
            },
            sendAsteroidsState:function(){
              this.connection.send(['AsteroidsState',this.isShow]);
            },
            activateAntiMater:function(){
              Axios.get('http://'+URL_REST+'/activateMater').then(
                  console.log("distribution activated")
              )
            },
            checkButtonState:function(){
              this.connection.send(['lever',"_"]);
              var _this = this;
              this.connection.onmessage = function (event) {
                    _this.buttonVisible = event.data === "true"
              }
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
            },

        },
    }
</script>

<style scoped>
    #sliderBar{
      margin-right: 20%;
      margin-left: 20%;
      top:650px;
      margin: 50 auto;
      width: 200px;
      height: 30px;
    }
     #roundslider{

      margin-right: 20%;
      margin-left: 20%;
      top:680px;
      margin: 50 auto;
      width: 200px;
      height: 30px;
    }
    #roundslider2{

      margin-right: 20%;
      margin-left: 65%;
      top:440px;
      margin: 50 auto;
      width: 200px;
      height: 30px;
    }
    .button1{
        position: absolute;
        top:550px;
        right: 690px;
        height: 6%;
        width: 7%;
        border-radius: 10%;
    }
    .button1Disabled{
      position: absolute;
      top:550px;
      right: 690px;
      height: 6%;
      width: 7%;
      border-radius: 10%;
      filter: grayscale(100%);
    }
    .button2{
        position: absolute;
        top:550px;
        left: 690px;
        height: 6%;
        width: 7%;
        border-radius: 10%;
    }
    .button2Disabled{
      position: absolute;
      top:550px;
      left: 690px;
      height: 6%;
      width: 7%;
      border-radius: 10%;
      filter: grayscale(100%);
    }
    .button5{
        position: absolute;
        top:490px;
        left: 400px;
        height: 6%;
        width: 7%;
        border-radius: 10%;
    }
    .button5Disabled{
      position: absolute;
      top:490px;
      left: 400px;
      height: 6%;
      width: 7%;
      border-radius: 10%;
      filter: grayscale(100%);
    }
    .button6{
        position: absolute;
        top:580px;
        left: 400px;
        height: 6%;
        width: 7%;
        border-radius: 10%;
    }
    .button6Disabled{
      position: absolute;
      top:580px;
      left: 400px;
      height: 6%;
      width: 7%;
      border-radius: 10%;
      filter: grayscale(100%);
    }
    .button7{
        position: absolute;
        top:490px;
        right: 400px;
        height: 6%;
        width: 7%;
        border-radius: 10%;
    }
    .button7Disabled{
      position: absolute;
      top:490px;
      right: 400px;
      height: 6%;
      width: 7%;
      border-radius: 10%;
      filter: grayscale(100%);
    }
    .button8{
        position: absolute;
        top:580px;
        right: 400px;
        height: 6%;
        width: 7%;
        border-radius: 10%;
    }
    .button8Disabled{
      position: absolute;
      top:580px;
      right: 400px;
      height: 6%;
      width: 7%;
      border-radius: 10%;
      filter: grayscale(100%);
    }
    .button9{
        position: absolute;
        top:50px;
        right: 47%;
        height: 6%;
        width: 7%;
        border-radius: 10%;
    }
    .button9Disabled{
      position: absolute;
      top:50px;
      right: 47%;
      height: 6%;
      width: 7%;
      border-radius: 10%;
      filter: grayscale(100%);
    }
    #button3{
        position: absolute;
        top:65%;
        left: 200px;
        height: 5%;
        width: 5%;
        border-radius: 10%;
    }
    .button10{
        position: absolute;
        top:800px;
        right: 1000px;
        height: 10%;
        width: 5%;
        border-radius: 10%;
    }
    .button10Disabled{
        position: absolute;
        top:800px;
        right: 1000px;
        height: 10%;
        width: 5%;
        border-radius: 10%;
        filter: grayscale(100%);
    }

    #b4{
        position: absolute;
        top:750px;
        left: 854px;
        height: 6%;
        width: 7%;
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
