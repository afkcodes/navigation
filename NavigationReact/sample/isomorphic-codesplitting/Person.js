var React = require('react');
var NavigationReact = require('navigation-react');
var NavigationBackLink = NavigationReact.NavigationBackLink;

/**
 * Registers the component creator for the Details State.
 */
exports.registerComponent = function(stateNavigator) {
    stateNavigator.states.person.createComponent = function(data) {
        return React.createElement(Details, data);
    }
}

var Details = React.createClass({
    render: function() {
        var person = this.props.person;
        return (
            React.createElement("div", {id: "details"}, 
                React.createElement(NavigationBackLink, {distance: 1, stateNavigator: this.props.stateNavigator}, "People"), 
                React.createElement("div", {id: "info"}, 
                    React.createElement("h2", null, person.name), 
                    React.createElement("div", {className: "label"}, "Date of Birth"), 
                    React.createElement("div", null, person.dateOfBirth), 
                    React.createElement("div", {className: "label"}, "Email"), 
                    React.createElement("div", null, person.email), 
                    React.createElement("div", {className: "label"}, "Phone"), 
                    React.createElement("div", null, person.phone)
                )
            )
        );
    }
})

