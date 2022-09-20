import { useParams, useNavigate } from 'react-router-dom';
import { useEffect, useContext } from 'react';
import { JobContext } from '../../context/JobContextProvider';
import { InfoContext, TYPE_ERROR} from '../../context/InfoContextProvider';

export function JobPageRedirect(props) {
	let { username, jobname } = useParams();
	let jobContext = useContext(JobContext);
	let navigate = useNavigate();
	let infoContext = useContext(InfoContext);

	useEffect(() => {
		jobContext.fetchMostJobsPossible(false, (infos) => {
			let jobInfo = infos.find((info) => {
				return info.config.name === jobname && info.user === username;
			});
			if (!jobInfo) {
				navigate('/jobs');
				infoContext.handleInformation(
					'Could not find job with name ' + jobname + ' of user ' + username
				, TYPE_ERROR);
			}
            else {
                navigate('/job/' + jobInfo.jobID);
            }
		});
	}, []);
}
