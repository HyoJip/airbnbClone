const authReducer = (state, action) => {
	switch (action.type) {
		default:
			return {
				id: "",
				name: "",
				email: "",
				password: "",
				profile: "",
				isLoggedIn: "",
			};
	}
};

export default authReducer;
