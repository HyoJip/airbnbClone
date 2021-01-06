import axios from "axios";

export const signup = user => {
	return axios.post("/api/1.0/users", user);
};
export const login = auth => {
	auth = { username: auth.email, password: auth.password };
	return axios.post("/api/1.0/login", {}, { auth });
};

export const setAuthorizationHeader = ({ email, password, isLoggedIn }) => {
	if (isLoggedIn) {
		axios.defaults.headers.common["Authorization"] = `Basic ${btoa(email + ":" + password)}`;
	} else {
		localStorage.clear();
		delete axios.defaults.headers.common["Authorization"];
	}
};
