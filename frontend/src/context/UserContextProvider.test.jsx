import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { UserContextProvider, UserContext, NO_TOKEN_AVAILABLE, LOCAL_STORAGE_TOKEN } from './UserContextProvider';
const jwt = require("jsonwebtoken")

const EXAMPLE_TOKEN =
	'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiYWRtaW4iLCJ1c2VybmFtZSI6InRlc3R1c2VyIn0.IxNQz1xtLmk1vZx3S7SrlwDFR0j9PyZoB29q5_Cbh-s';
const decoded = jwt.decode(EXAMPLE_TOKEN);
function DummyDisplay(props) {
	return (
		<UserContextProvider>
			<UserContext.Consumer>
				{({ user, login, logout }) => (
					<div>
						<button onClick={() => logout()}>Logout</button>
						<button onClick={() => login(EXAMPLE_TOKEN)}>Login</button>
						<div data-testid='dummyDisplayToken'>{user.token}</div>
						<div data-testid='dummyDisplayUsername'>{user.username}</div>
						<div data-testid='dummyDisplayRole'>{user.role}</div>
					</div>
				)}
			</UserContext.Consumer>
		</UserContextProvider>
	);
}

const localStorageMock = (function() {
  let store = {}

  return {
    getItem: function(key) {
      return store[key] || null
    },
    setItem: function(key, value) {
      store[key] = value.toString()
    },
    removeItem: function(key) {
      delete store[key]
    },
    clear: function() {
      store = {}
    }
  }
})()
global.localStorage= localStorageMock;
afterEach(() => {
    localStorage.clear();
})
test("Context should have default values", () => {
    render(<DummyDisplay/>);
    expect(screen.getByTestId("dummyDisplayToken").textContent).toBe(NO_TOKEN_AVAILABLE);
})

test("context should get token from localstorage on load",async () => {
    localStorage.setItem(LOCAL_STORAGE_TOKEN, EXAMPLE_TOKEN);
    render(<DummyDisplay/>)
    expect(screen.getByTestId("dummyDisplayToken").textContent).toBe(EXAMPLE_TOKEN)
    waitFor( () => expect(screen.getByTestId("dummyDisplayUsername").textContext).toBe(decoded.username))
})


test("context can log user in",async () => {
    render(<DummyDisplay/>);
    expect(screen.getByTestId("dummyDisplayToken").textContent).toBe(NO_TOKEN_AVAILABLE);
    fireEvent.click(screen.getByText("Login"));
    waitFor(() => expect(screen.getByTestId("dummyDisplayToken").textContent).toBe(EXAMPLE_TOKEN));
   expect(screen.getByTestId("dummyDisplayRole").textContent).toBe(decoded.role)
   expect(screen.getByTestId("dummyDisplayUsername").textContent).toBe(decoded.username)
})

test("context can log user out", async () => {
    localStorage.setItem(LOCAL_STORAGE_TOKEN, EXAMPLE_TOKEN);
    render(<DummyDisplay/>);
    fireEvent.click(screen.getByText("Logout"));
    waitFor(() => expect(screen.getByTestId("dummyDisplayToken").textContent).toBe(NO_TOKEN_AVAILABLE))
   expect(screen.getByTestId("dummyDisplayRole").textContent).toBe("")
   expect(screen.getByTestId("dummyDisplayUsername").textContent).toBe("")
    
    
})
