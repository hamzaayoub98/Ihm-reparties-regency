<template>
    <div id="home" >
        <Asteroid ></Asteroid>
        <button v-on:click="action(1)">
            <img  id="button1"  src="../assets/blue_button.png">
        </button>
        <button v-on:click="action(2)">
            <img  id="button2"  src="../assets/kc.png">
        </button>
        <button v-on:click="sendPing()">
            <img  id="button3"  src="../assets/send.png">
        </button>

    </div>
</template>

<script>
    import  Axios from 'axios';
    import Asteroid from "@/components/Asteroid";
    export default {
        name: "Home",
        components:{Asteroid},
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
            Axios.get("http://localhost:3000")
            .then(response =>(this.info = response,
            console.log(response)));
        },
        methods: {
            action:function (number) {
                Axios.post('http://localhost:3000/button', {
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
                this.connection = new WebSocket("ws://localhost:4000/")
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
    #button3{
        position: absolute;
        top:95%;
        right: 1000px;
        height: 5%;
        width: 5%;
        border-radius: 10%;
    }
    #home{
        width: 100%;
        height: 100%;
        position: absolute;
    }
    
    
    
</style>
