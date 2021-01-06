import { applyMiddleware, createStore } from "redux";
import authReducer from "./authReducer";
import logger from "redux-logger";
import thunk from "redux-thunk";
import * as apiCalls from "../api/apiCalls";

const configureStore = (addLogger = true) => {
	const middleware = addLogger ? applyMiddleware(logger, thunk) : applyMiddleware(thunk);
	const localStorageData = localStorage.getItem("USER_AUTH");

	let persistedState = {
		id: "",
		email: "",
		name: "",
		password: "",
		profile: "",
		status: "",
		isLoggedIn: false,
	};

	if (localStorageData) {
		try {
			persistedState = JSON.parse(localStorageData);
			apiCalls.setAuthorizationHeader(persistedState);
		} catch (error) {
			console.error(error);
		}
	}

	const store = createStore(authReducer, persistedState, middleware);

	store.subscribe(() => {
		localStorage.setItem("USER_AUTH", JSON.stringify(store.getState()));
		apiCalls.setAuthorizationHeader(store.getState());
	});

	return store;
};

export default configureStore;
