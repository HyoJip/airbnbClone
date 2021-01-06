import React from "react";
import { Route, Switch } from "react-router-dom";
import HomePage from "../pages/HomePage";
import Header from "../pages/partials/Header";
import Footer from "../pages/partials/Footer";
import "../scss/__base.scss";

const App = () => {
	return (
		<div>
			<Header />
			<div className="container">
				<Switch>
					<Route exact path="/" component={HomePage} />
				</Switch>
			</div>
			<Footer />
		</div>
	);
};

export default App;
