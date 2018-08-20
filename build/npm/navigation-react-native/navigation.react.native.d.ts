import { Component, ReactNode } from 'react';
import { ImageURISource } from 'react-native';
import { StateNavigator, State } from 'navigation';

/**
 * Adds handlers for JavaScript and Native Navigate events
 * @param stateNavigator State Navigator(s) that correspond to Native stack(s)
 */
export function addNavigateHandlers(stateNavigator: StateNavigator | StateNavigator[]);

/**
 * Defines the Scene Props contract
 */
export interface SceneProps {
    crumb?: number;
    renderScene?: (state: State, data: any) => ReactNode;
}

/**
 * Renders the scene for the crumb
 */
export class Scene extends Component<SceneProps> { }

/**
 * Renders buttons in the left UI bar
 */
export class LeftBarIOS extends Component { }

/**
 * Renders buttons in the right UI bar
 */
export class RightBarIOS extends Component { }

/**
 * Defines the Bar Button Props contract
 */
export interface BarButtonIOSProps {
    title?: string;
    image?: ImageURISource;
    onPress?: () => void;
}

/**
 * Renders a button in the UI bar
 */
export class BarButtonIOS extends Component<BarButtonIOSProps> { }
