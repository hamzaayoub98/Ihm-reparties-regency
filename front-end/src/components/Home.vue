<template>
    <div id="home" >
        <Asteroid  v-show="isShow"></Asteroid>
        <AsteroidLeft v-show="isShow"></AsteroidLeft>
        <div class="box">
            <ToggleButton />
        </div>
        <button v-on:click="action(1)">
            <img  id="button1"  src="../assets/blue_button.png">
        </button>
        <VueSlider v-model="sliderValue" id="slider" v-on:change="sendSliderValue"/>
        <button v-on:click="action(2)">
            <img  id="button2"  src="../assets/redButton.png">
        </button>
        <button v-on:click="sendPing()">
            <img  id="button3"  src="../assets/send.png">
        </button>
        <button @click="isShow = !isShow" id="asteroidsVue" v-on:click="sendAsteroidsState()" >Hide Asteroids</button>
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
            }
        },
        props: {
            msg: String,
        },
        created: function() {
            this.initWSConnection();
        },
        mounted() {
            console.log("🚀 ~ file: Home.vue ~ line 43 ~ mounted ~ URL_REST", URL_REST)
            Axios.get("http://" + URL_REST)
            .then(response =>(this.info = response,
            console.log(response)));
        },
        methods: {
            action:function (number) {
                Axios.post("http://" + URL_REST + "action", {
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
