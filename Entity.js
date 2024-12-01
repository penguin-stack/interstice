class Entity {
    constructor(x, y, color) {
        this.x = x;
        this.y = y;
        this.mesh = this.createMesh(color);
        this.mesh.position.set((x + 0.5) * cellSize, 0.1, (y + 0.5) * cellSize);
        scene.add(this.mesh);
    }

    createMesh(color) {
        const geometry = new THREE.BoxGeometry(0.5 * cellSize, 0.5 * cellSize, 0.5 * cellSize);
        const material = new THREE.MeshBasicMaterial({ color: color });
        return new THREE.Mesh(geometry, material);
    }

    removeFromScene() {
        scene.remove(this.mesh);
    }

}