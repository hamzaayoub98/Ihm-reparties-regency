<template>
    <div id="home" >
        <button v-on:click="sendPing()">Send WS PING</button>
        <h1>Hi m8</h1>
        <button v-on:click="action(1)">
            <img  id="button1"  src="../assets/blue_button.png">
        </button>
        <button v-on:click="action(2)">
            <img  id="button2"  src="../assets/kc.png">
        </button>
        <Asteroid></Asteroid>
    </div>
</template>

<script>
    import  Axios from 'axios';
    import Asteroid from "./Asteroid";
    import { URL_REST, URL_WS } from '../main.js' 
    export default {
        name: "Home",
        components: {Asteroid},
        data(){
            return {
                info : null,
                photoSrc:["../assets/blue_button.png","../assets/kc.png"],
                mySrc:0,
                connection: null,
            }
        },
        created: function() {
            this.initWSConnection();
        },
        mounted() {
            Axios.get("http://" + URL_REST)
            .then(response =>(this.info = response,
            console.log(response)));
        },
        methods: {
            action:function (number) {
                Axios.post('http://" + URL_REST + "/button', {
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
                this.connection = new WebSocket("ws://" + URL_WS)
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

            }
        },
    }
</script>

<style scoped>
    
    #button1{
        position: absolute;
        top:600px;
        right: 500px;
        height: 5%;
        width: 5%;
        border-radius: 10%;
    }
    #button2{
        position: absolute;
        top:600px;
        right: 1000px;
        height: 5%;
        width: 5%;
        border-radius: 10%;
    }
    
</style>
