<template>
    <div id="container"></div>
</template>

<script>
    import * as THREE from "three";
    
    export default {
        name: "Asteroid",
        data(){
            return {
                camera: null,
                scene: null,
                renderer: null,
                mesh: null,
                geometry:null,
                material:null,
                light:null,
                emitters:[],
                controls:null,
                timer: 0
        }
        },
        methods:{
            init: function() {

                let container = document.getElementById('container');
                //let  loader = new GLTFLoader();
                
                this.camera = new THREE.PerspectiveCamera(50, window.innerWidth / window.innerHeight, 1, 10000);
                //this.camera = new THREE.PerspectiveCamera(70, container.clientWidth/container.clientHeight, 0.1, 10);
                this.camera.position.z = 1;
                this.camera.position.y = 500;
                this.camera.position.x = 500;
                this.scene = new THREE.Scene();
                this.camera.lookAt(this.scene.position);
                this.scene.add(this.camera);
                this.light = new THREE.DirectionalLight(0xE0E0FF, 1.5);
                this.light.position.set(200, 500, 200);
                this.scene.add(this.light);
                this.asteroids();
                this.renderer = new THREE.WebGLRenderer({ alpha: true });
                this.renderer.setSize(container.clientWidth, container.clientWidth);

                //controls = new THREE.OrbitControls(camera, renderer.domElement);
                container.appendChild(this.renderer.domElement);
                
               
               
            },
            animate: function() {
                this.updateEmitters();
                requestAnimationFrame(this.animate);
                this.render();
            },
            render() {
                this.renderer.render(this.scene, this.camera);
            },
            asteroids() {
                this.material = new THREE.MeshPhongMaterial({
                color: 0x0072c6,
                shading: THREE.FlatShading,
                emissive: 0.5,
                shininess: 0
                });
                this.mesh = new THREE.Mesh(new THREE.OctahedronGeometry(15, 1), this.material);
                var emitter1 = this.createEmitter({
                    size: {
                    x: 100,
                    y: 100,
                    z: 500
                    },
                    pos: {
                    x: 0,
                    y: 0,
                    z: -250
                    },
                    count: 70,
                    obj: {
                    mesh: this.mesh,
                    size: [0.2, 1]
                    }
                    });
                    this.emitters.push(emitter1);
                },
                createEmitter(opts) {
                var parent = new THREE.Object3D();
                for (var i = 0; i < opts.count; i++) {
                    this.mesh = opts.obj.mesh.clone();
                    // Initial positions
                    this.mesh.position.x = this.randomRange(0, opts.size.x);
                    this.mesh.position.y = this.randomRange(0, opts.size.y);
                    this.mesh.position.z = this.randomRange(0, opts.size.z);
                    // Sizes
                    var meshSize = this.randomRange(opts.obj.size[0], opts.obj.size[1]);
                    this.mesh.scale.set(meshSize, meshSize, meshSize);
                    parent.add(this.mesh);
                }
                parent.position.set(opts.pos.x, opts.pos.y, opts.pos.z);
                parent.boxLength = opts.size.z;
                parent.boxheight = opts.size.y;
                //this.scene.add(new THREE.BoxHelper(parent, 0xff0000));
                this.scene.add(parent);
                return parent;
                },
                updateEmitters() {
                this.timer += 0.03;
                this.emitters.forEach(function(emitter) {
                    for (var i = 0; i < emitter.children.length; i++) {
                    // Moving loop
                    if (emitter.children[i].position.z < emitter.boxLength) {
                        emitter.children[i].position.z += 2;
                    } else {
                        // Move mesh back to the begining of the volume
                        emitter.children[i].position.z -= emitter.boxLength;
                    }
                    // Rotate
                    emitter.children[i].rotation.x -= 0.02;
                    emitter.children[i].rotation.z -= 0.02;
                    }
                });
                },
                randomRange(min, max) {
                    return Math.random() * (max - min) + min;
                }
        },
        mounted() {
            this.init();
            this.animate();
        }
    }
</script>
<style scoped>
    #container{
        height: 465px;
        width: 465px;
        top: 0%;
        margin: 0 auto;   
    }
</style>