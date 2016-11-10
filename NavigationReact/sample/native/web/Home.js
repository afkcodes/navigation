import React from 'react';

export default () => (
    <div>
        <div style={styles.nav}>
            <svg style={styles.home} viewBox="0 0 64 72"><g><path d="M60.034 33.795l-26-23.984a2.997 2.997 0 0 0-4.068 0l-26 23.984a3 3 0 0 0 4.068 4.41l2.265-2.09 6.809 24.683A3 3 0 0 0 20 63h24a3 3 0 0 0 2.892-2.202l6.809-24.683 2.265 2.09a2.988 2.988 0 0 0 2.033.795 3 3 0 0 0 2.035-5.205zM32 53a3 3 0 1 1 0-6 3 3 0 0 1 0 6zm0-11a6.999 6.999 0 1 1 0-13.998A7 7 0 1 1 32 42z"></path></g></svg>
            <h1 style={styles.h1}>Home</h1>
        </div>
        <ul>
            <li>
                <h2>React</h2> <span>@reactjs</span><span>3h</span>
                <img src="react.png" />
                <div>Have you tried our 15.4 release candidate? Give it a shot and let us know if you have any issues before we ship it! </div>
            </li>
            <li>
                <h2>React</h2> <span>@reactjs</span><span>3h</span>
                <img src="react.png" />
                <div>Have you tried our 15.4 release candidate? Give it a shot and let us know if you have any issues before we ship it! </div>
            </li>
            <li>
                <h2>React</h2> <span>@reactjs</span><span>3h</span>
                <img src="react.png" />
                <div>Have you tried our 15.4 release candidate? Give it a shot and let us know if you have any issues before we ship it! </div>
            </li>
            <li>
                <h2>React</h2> <span>@reactjs</span><span>3h</span>
                <img src="react.png" />
                <div>Have you tried our 15.4 release candidate? Give it a shot and let us know if you have any issues before we ship it! </div>
            </li>
            <li>
                <h2>React</h2> <span>@reactjs</span><span>3h</span>
                <img src="react.png" />
                <div>Have you tried our 15.4 release candidate? Give it a shot and let us know if you have any issues before we ship it! </div>
            </li>
            <li>
                <h2>React</h2> <span>@reactjs</span><span>3h</span>
                <img src="react.png" />
                <div>Have you tried our 15.4 release candidate? Give it a shot and let us know if you have any issues before we ship it! </div>
            </li>
        </ul>
    </div>
);

var styles = {
    home: {
        display: 'inline-block',
        fill: '#1da1f2',
        width: '10%',
        marginLeft: '5%',
        verticalAlign: 'text-bottom'
    },
    h1: {
        fontSize: '120%',
        marginLeft: '5%',
        display: 'inline'
    },
    nav: {
        padding: '10px 0',
        boxShadow: '0 0 4px #657786'
    }    
}