import * as apiCalls from "../api/apiCalls";

export const logoutHandler = () => ({ type: "LOGOUT_SUCCESS" });

export const loginSuccess = (response, auth) => ({
	type: "LOGIN_SUCCESS",
	payload: {
		...response.data,
		password: auth.password,
	},
});

export const loginHandler = auth => dispatch =>
	apiCalls.login(auth).then(response => {
		dispatch(loginSuccess(response, auth));
		return response;
	});

export const signupHandler = user => dispatch =>
	apiCalls.signup(user).then(response => {
		dispatch(loginHandler(user, user.password));
		return response;
	});
