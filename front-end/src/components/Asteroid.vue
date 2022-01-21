<template>
    <div id="container"></div>
</template>

<script>
    import * as THREE from "three";
    import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader';

    export default {
        name: "Asteroid",
        data(){
            return {
                camera: null,
                scene: null,
                renderer: null,
                mesh: null,
        }
        },
        methods:{
            init: function() {
                let container = document.getElementById('container');
                let  loader = new GLTFLoader();



                this.camera = new THREE.PerspectiveCamera(70, container.clientWidth/container.clientHeight, 0.01, 10);
                this.camera.position.z = 1;

                this.scene = new THREE.Scene();

                loader.load('../assets/Bennu.glb',function (glb) {
                    let sword = glb.scene;  // sword 3D object is loaded
                    sword.scale.set(2, 2, 2);
                    sword.position.y = 4;
                    this.scene.add( glb.scene);

                }, undefined, function ( error ) {

                    console.error( 'Une erreur est survenue',error );
                })

                let geometry = new THREE.BoxGeometry(0.2, 0.2, 0.2);
                let material = new THREE.MeshNormalMaterial();

                this.mesh = new THREE.Mesh(geometry, material);
                this.scene.add(this.mesh);

                this.renderer = new THREE.WebGLRenderer({antialias: true});
                this.renderer.setSize(container.clientWidth, container.clientHeight);
                container.appendChild(this.renderer.domElement);

            },
            animate: function() {
                requestAnimationFrame(this.animate);
                this.mesh.rotation.x += 0.01;
                this.mesh.rotation.y += 0.02;
                this.renderer.render(this.scene, this.camera);
            },
            renderScene() {
                this.renderer.render(this.scene, this.camera);
            },
        },
        mounted() {
            this.init();
            this.animate();
        }
    }
</script>

<style scoped>
    #container{
        height: 300px;
        width: 700px;
    }
</style>
