import { GraphicEntityModule } from './entity-module/GraphicEntityModule.js';
import { TooltipModule } from './modules/TooltipModule.js';
import { FXModule, api } from './modules/FXModule.js'

// List of viewer modules that you want to use in your game
export const modules = [
	GraphicEntityModule , TooltipModule, FXModule
];

export const playerColors = [
      '#c4001a',
      '#2700c8',
      '#1fb90b',
      '#c6cc1c'
    ];

export const gameName = 'Vindinium';


export const options = [{
  title: 'SHOW DEBUG GRID',
  get: function () {
    return api.showGrid
  },
  set: function (value) {
    api.showGrid = value
  },
  values: {
    'ON': true,
    'OFF': false
  }
}]