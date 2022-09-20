import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import {
	UserContextProvider,
	UserContext,
	NO_TOKEN_AVAILABLE,
	LOCAL_STORAGE_TOKEN,
    ROLE_USER,
    ROLE_ADMIN,
} from './UserContextProvider';
const jwt = require('jsonwebtoken');

const normalUserVerifiedToken =
	'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzaW1vbiIsImV4cCI6MTY2NTQ1Nzg5OCwiaWF0IjoxNjYzNjU3ODk4LCJhdXRob3JpdGllcyI6IltOT1JNQUxfVVNFUiwgVmVyaWZpZWRdIn0.MAp0ThOLkU8FgWtEJI1fgNjtmooigR0ljG4r2U6t9rTGBTDeEUeAH3rMIcfbU_6ZTJs7PTFrfpMORav2pnQY_g';
const normalUserUnverifiedToken =
	'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzaW1vbiIsImV4cCI6MTY2NTQ1Nzk2MSwiaWF0IjoxNjYzNjU3OTYxLCJhdXRob3JpdGllcyI6IltOT1JNQUxfVVNFUiwgTm90IHZlcmlmaWVkXSJ9.Ugsd3K896KWqvHKEc5GHIZz5q_ZknXlLNWAuyhpuvPJxhv8nr9AxJlnveeELC33ljxEOcD-Qgf0jlOlDKaTDQw';
const adminVerifiedToken =
	'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzaW1vbiIsImV4cCI6MTY2NTQ1ODAxOCwiaWF0IjoxNjYzNjU4MDE4LCJhdXRob3JpdGllcyI6IltBRE1JTiwgVmVyaWZpZWRdIn0.ZX-2MCrfPzGnblZe9zwCKxA7YDPCNyZXhbaflnjhNHShleBxV22D40a-njxJEOOeffugKm1DK6DsEp3rrEpCVw';
const adminUnverifiedToken =
	'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzaW1vbiIsImV4cCI6MTY2NTQ1ODA5MiwiaWF0IjoxNjYzNjU4MDkyLCJhdXRob3JpdGllcyI6IltBRE1JTiwgTm90IHZlcmlmaWVkXSJ9.qLa0oFaxFjx9idfV9-Y0cFDoHVg8Tsdo3_tU0oj1tGeR8m_QXh2wuJM4er1X2gSzup7wAgwQAgQtXtitAl0MHQ';
const username = 'simon';
function DummyDisplay(props) {
	return (
		<UserContextProvider>
			<UserContext.Consumer>
				{({ user, login, logout }) => (
					<div>
						<button onClick={() => logout()}>Logout</button>
						<button onClick={() => login(normalUserVerifiedToken)}>
							LoginVerifiedUser
						</button>
						<button onClick={() => login(normalUserUnverifiedToken)}>
							LoginUnverifiedUser
						</button>
						<button onClick={() => login(adminVerifiedToken)}>
							LoginVerifiedAdmin
						</button>
						<button onClick={() => login(adminUnverifiedToken)}>
							LoginUnverifiedAdmin
						</button>
						<div data-testid='dummyDisplayToken'>{user.token}</div>
						<div data-testid='dummyDisplayUsername'>{user.username}</div>
						<div data-testid='dummyDisplayRole'>{user.role}</div>
                        <div data-testid='dummyDisplayIsVerified'>{user.isVerified ? "true" : "false"}</div>
					</div>
				)}
			</UserContext.Consumer>
		</UserContextProvider>
	);
}

const localStorageMock = (function () {
	let store = {};

	return {
		getItem: function (key) {
			return store[key] || null;
		},
		setItem: function (key, value) {
			store[key] = value.toString();
		},
		removeItem: function (key) {
			delete store[key];
		},
		clear: function () {
			store = {};
		},
	};
})();
global.localStorage = localStorageMock;
afterEach(() => {
	localStorage.clear();
});
test('Context should have default values', () => {
	render(<DummyDisplay />);
	expect(screen.getByTestId('dummyDisplayToken').textContent).toBe(
		NO_TOKEN_AVAILABLE
	);
});

test('context should get token from localstorage on load', async () => {
	localStorage.setItem(LOCAL_STORAGE_TOKEN, normalUserVerifiedToken);
	render(<DummyDisplay />);
	expect(screen.getByTestId('dummyDisplayToken').textContent).toBe(
		normalUserVerifiedToken
	);
	waitFor(() =>
		expect(screen.getByTestId('dummyDisplayUsername').textContext).toBe(
			username
		)
	);
});

test('context can log normal unverified user in', async () => {
	render(<DummyDisplay />);
	expect(screen.getByTestId('dummyDisplayToken').textContent).toBe(
		NO_TOKEN_AVAILABLE
	);
	fireEvent.click(screen.getByText('LoginUnverifiedUser'));
	waitFor(() =>
		expect(screen.getByTestId('dummyDisplayToken').textContent).toBe(
            normalUserUnverifiedToken
		)
	);
	expect(screen.getByTestId('dummyDisplayRole').textContent).toBe(ROLE_USER);
	expect(screen.getByTestId('dummyDisplayUsername').textContent).toBe(
		username
	);
    expect(screen.getByTestId('dummyDisplayIsVerified').textContent).toBe("false")
});
test('context can log normal verified user in', async () => {
	render(<DummyDisplay />);
	expect(screen.getByTestId('dummyDisplayToken').textContent).toBe(
		NO_TOKEN_AVAILABLE
	);
	fireEvent.click(screen.getByText('LoginVerifiedUser'));
	waitFor(() =>
		expect(screen.getByTestId('dummyDisplayToken').textContent).toBe(
            normalUserUnverifiedToken
		)
	);
	expect(screen.getByTestId('dummyDisplayRole').textContent).toBe(ROLE_USER);
	expect(screen.getByTestId('dummyDisplayUsername').textContent).toBe(
		username
	);
    expect(screen.getByTestId('dummyDisplayIsVerified').textContent).toBe("true")
});
test('context can log  unverified admin in', async () => {
	render(<DummyDisplay />);
	expect(screen.getByTestId('dummyDisplayToken').textContent).toBe(
		NO_TOKEN_AVAILABLE
	);
	fireEvent.click(screen.getByText('LoginUnverifiedAdmin'));
	waitFor(() =>
		expect(screen.getByTestId('dummyDisplayToken').textContent).toBe(
            normalUserUnverifiedToken
		)
	);
	expect(screen.getByTestId('dummyDisplayRole').textContent).toBe(ROLE_ADMIN);
	expect(screen.getByTestId('dummyDisplayUsername').textContent).toBe(
		username
	);
    expect(screen.getByTestId('dummyDisplayIsVerified').textContent).toBe("false")
});
test('context can log  verified admin in', async () => {
	render(<DummyDisplay />);
	expect(screen.getByTestId('dummyDisplayToken').textContent).toBe(
		NO_TOKEN_AVAILABLE
	);
	fireEvent.click(screen.getByText('LoginVerifiedAdmin'));
	waitFor(() =>
		expect(screen.getByTestId('dummyDisplayToken').textContent).toBe(
            normalUserUnverifiedToken
		)
	);
	expect(screen.getByTestId('dummyDisplayRole').textContent).toBe(ROLE_ADMIN);
	expect(screen.getByTestId('dummyDisplayUsername').textContent).toBe(
		username
	);
    expect(screen.getByTestId('dummyDisplayIsVerified').textContent).toBe("true")
});

test('context can log user out', async () => {
	localStorage.setItem(LOCAL_STORAGE_TOKEN, normalUserVerifiedToken);
	render(<DummyDisplay />);
	fireEvent.click(screen.getByText('Logout'));
	waitFor(() =>
		expect(screen.getByTestId('dummyDisplayToken').textContent).toBe(
			NO_TOKEN_AVAILABLE
		)
	);
	expect(screen.getByTestId('dummyDisplayRole').textContent).toBe('');
	expect(screen.getByTestId('dummyDisplayUsername').textContent).toBe('');
});
