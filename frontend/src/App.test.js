import { render, screen } from '@testing-library/react';
import App from './App';

test('renders', () => {
    render(<App/>)
    expect(screen.getByText("Welcome to Mallob!")).not.toBeNull();
});
