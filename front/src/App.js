import UserSignupPage from "./pages/UserSignupPage";
import * as apiCalls from "./api/apiCalls";
import LoginPage from "./pages/LoginPage";

const actions = {
	postLogin: apiCalls.login,
};

function App() {
	return <LoginPage actions={actions} />;
}

export default App;
