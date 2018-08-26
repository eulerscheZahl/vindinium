import { GraphicEntityModule } from './entity-module/GraphicEntityModule.js';
import { TooltipModule } from './modules/TooltipModule.js';

// List of viewer modules that you want to use in your game
export const modules = [
	GraphicEntityModule, TooltipModule
];

export const playerColors = [
      '#c4001a',
      '#2700c8',
      '#1fb90b',
      '#c6cc1c'
    ];

export const gameName = 'Vindinium';
