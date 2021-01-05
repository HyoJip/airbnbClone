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
		default:
			return state;
	}
};

export default authReducer;
