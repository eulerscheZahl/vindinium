import * as utils from '../core/utils.js';
import { api as entityModule } from '../entity-module/GraphicEntityModule.js';

export const api = {
  showGrid: false
}

export class FXModule {
  constructor (assets) {
  }

  static get name () {
    return 'fx'
  }

  updateScene (previousData, currentData, progress) {
    var entity = entityModule.entities.get(this.entityId);

    entity.container.visible = api.showGrid;
  }

  handleFrameData (frameInfo, nothing) {
    return {...frameInfo}
  }

  reinitScene (container, canvasData) {
  }

  animateScene (delta) {
  }

  handleGlobalData (players, entityId) {
    this.entityId = entityId;
  }
}