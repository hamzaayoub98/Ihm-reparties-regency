<template>
    <div id="home">
        <h1>Hi m8</h1>
        <img alt="home screen" src="../assets/logo.png">
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
            }
        },
        mounted() {
            Axios.get("https://localhost:3000")
            .then(response =>(this.info = response));
        }
    }
</script>

<style scoped>
    #home{
    color: #ff0028;
    }
    #api{
        color: chartreuse;
    }
</style>
