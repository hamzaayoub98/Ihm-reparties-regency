<template>
    <div id="home">
        <h1>Hi m8</h1>
        <img alt="home screen" src="../assets/kc.png">
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
            Axios.get("https://api.themoviedb.org/3/person/popular?api_key=f8c59b73c44d9240c1ded0a07da0d5f5")
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
