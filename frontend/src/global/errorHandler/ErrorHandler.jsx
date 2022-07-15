import React from 'react';

export class ErrorHandler extends React.Component {
	constructor(props) {
		super(props);
		this.state = { errorMessage: '', hasError: false };
	}
	static getDerivedStateFromError(appError) {
		return { hasError: true };
	}
	componentDidCatch(appError, errorInfo) {
		if (process.env.NODE_ENV === 'development') {
			console.log(appError.getMessage());
		}
		this.setState((state) => ({
			errorMessage: appError.getMessage()
		}));
	}

	render() {
		return (
			<div>
				<div>Error: {this.state.errorMessage}</div>
				{this.state.hasError && (
					<button
						onClick={() => this.setState({ hasError: false, errorMessage: '' })}
					>
						Close error
					</button>
				)}
				{this.props.children}
			</div>
		);
	}
}
