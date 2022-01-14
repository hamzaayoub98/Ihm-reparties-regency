<template>
    <div id="home">
        <h1>Hi m8</h1>
        <button v-on:click="myToggle">
            <img  id="cockpit"  src="../assets/blue_button.png">
        </button>
        <div id="api">
            <p>
                {{info}}
            </p>
        </div>
    </div>
</template>

<script>
    import  Axios from 'axios';
    export default {
        name: "Home",
        data(){
            return {
                info : null,
                photoSrc:["./assets/blue_button.png","../assets/kc.png"],
                mySrc:0,
            }
        },
        mounted() {
            Axios.get("http://localhost:3000")
            .then(response =>(this.info = response));
        },
        methods: {
            myToggle:function () {

            }
        },
    }
</script>

<style scoped>
    #cockpit{
        position: absolute;
        top:600px;
        right: 500px;
        height: 5%;
        width: 5%;
        border-radius: 10%;
    }
    #home{
        color: #ff0028;
        background-image: url("../assets/cockpit.png");
        height: 2000px;
        width: 100%;
    }
    #api{
        color: chartreuse;
    }
</style>
