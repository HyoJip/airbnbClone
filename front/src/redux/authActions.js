import * as apiCalls from "../api/apiCalls";

export const signupHandler = user => apiCalls.signup(user);
