import { SharedItem } from './Props';

class SharedElementRegistry {
    private sharedElements: { [scene: number]: { [name: string]: { ref: HTMLElement, data: any } } } = {};
    private update;
    constructor(update = () => {}) {
        this.update = update;
    }
    registerSharedElement(scene: number, name: string, ref: HTMLElement, data) {
        this.sharedElements[scene] = this.sharedElements[scene] || {};
        this.sharedElements[scene][name] = {ref, data};
        this.update();
    }
    unregisterSharedElement(scene: number, name?: string) {
        if (this.sharedElements[scene]) {
            if (name)
                delete this.sharedElements[scene][name];
            else
                delete this.sharedElements[scene];
            this.update();
        }
    }
    getSharedElements(scene: number, oldScene: number) {
        if (scene === oldScene)
            return [];
        var oldSharedElements = this.sharedElements[oldScene];
        var mountedSharedElements = this.sharedElements[scene];
        var sharedElements: SharedItem[] = [];
        for(var name in mountedSharedElements) {
            if (oldSharedElements && oldSharedElements[name]) {
                sharedElements.push({
                    name,
                    oldElement: oldSharedElements[name],
                    mountedElement: mountedSharedElements[name]
                });
            }
        }
        return sharedElements.sort((a, b) => a.name.localeCompare(b.name));
    }
}
export default SharedElementRegistry;
