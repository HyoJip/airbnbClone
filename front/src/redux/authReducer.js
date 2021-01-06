const initialState = {
	id: "",
	email: "",
	name: "",
	password: "",
	profile: "",
	status: "",
	isLoggedIn: false,
};

const authReducer = (state = initialState, action) => {
	switch (action.type) {
		case "LOGOUT_SUCCESS":
			return { ...initialState };
		case "LOGIN_SUCCESS":
			return { ...action.payload, isLoggedIn: true };
		default:
			return state;
	}
};

export default authReducer;
