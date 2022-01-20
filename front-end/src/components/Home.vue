<template>
    <div id="home" >
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
    export default {
        name: "Home",
        components: {Asteroid},
        data(){
            return {
                info : null,
                photoSrc:["../assets/blue_button.png","../assets/kc.png"],
                mySrc:0,
            }
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
