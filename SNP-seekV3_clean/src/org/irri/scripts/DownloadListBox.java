package org.irri.scripts;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import org.irri.iric.ds.chado.domain.GenotypeRunPlatform;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.admin.AsyncJob;
import org.irri.iric.portal.admin.AsyncJobImpl;
import org.irri.iric.portal.admin.JobsFacade;
import org.irri.iric.portal.genotype.GenotypeFacade;
import org.irri.iric.portal.genotype.GenotypeQueryParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class DownloadListBox {

	private String contig;
	private Long lStart;
	private Long lStop;
	private Set dataset;

	@Autowired
	@Qualifier("GenotypeFacade")
	private GenotypeFacade genotype;
	
	@Autowired
	@Qualifier("JobsFacade")
	private JobsFacade jobsfacade_orig;
	private Set snpset;
	
	
	

	public DownloadListBox(String contig, Set dataset, Long lStart, Long lStop) {
		this.contig = contig;
		this.lStart = lStart;
		this.lStop = lStop;
		this.dataset = dataset;
	}

	public DownloadListBox(String contig, Set dataset) {
		this.contig = contig;
		this.dataset = dataset;
		this.snpset = dataset;
		
	}

	public DownloadListBox(String contig, Set dataset, Set snpset) {
		this.contig = contig;
		this.dataset = dataset;
		this.snpset = snpset;
		
	}

	public void downloadBigListBox(String str, String contig) throws Exception {

		try {
			genotype = (GenotypeFacade) AppContext.checkBean(genotype, "GenotypeFacade");

			int size = genotype.getFeatureLength(contig, AppContext.getDefaultOrganism());
//			int size = 1000;
			int incrementSize = 500000;
			int lStart = 0;
			int lStop = 1;

			while (lStart < size) {
				lStop = lStart + incrementSize;

				if (lStop > size)
					lStop = size;

				downloadList(AppContext.getTempDir() + "snp3kvars-" + str + ":" + (lStart + 1) + "-" + lStop + ".csv", ",",
						"csv", new Long(lStart + 1), new Long(lStop));

//				callreport = callableDownloadBigListbox(
//						AppContext.getTempDir() + "snp3kvars-" + str+"----"+ lStart +".csv", ",", "csv", new Long(lStart + 1), new Long(lStop));

				System.out.println(AppContext.getTempDir() + "snp3kvars-" + str + ":" + lStart + "-" + lStop + ".csv");

//				AsyncJobReport report = callreport.call();

				lStart = lStart + incrementSize;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		 executor.shutdown();
		// AsyncJobReport report=null;

	}

	private void downloadList(final String filename, final String delimiter,
			final String format, Long lStart, Long lStop) {
		String msg = "";
		String jobid = "";
		String url = "";
		Future future = null;

		genotype = (GenotypeFacade) AppContext.checkBean(genotype, "GenotypeFacade");

		try {

			genotype = (GenotypeFacade) AppContext.checkBean(genotype, "GenotypeFacade");

//			lStart = new Long(1);
//			lStop = new Long(500000);
////			lStop = new Long(genotype.getFeatureLength(contig, AppContext.getDefaultOrganism()));

			Set runs = new HashSet(getGenotyperun());

			GenotypeQueryParams params = new GenotypeQueryParams(null, contig, lStart, lStop, true, false, snpset,
					dataset, runs, false, null, null, null, true, false);

			if (format.equals("plink"))
				params.setFilename(filename + ".plink");
			else if (format.equals("flapjack"))
				params.setFilename(filename + ".flapjack");
			else {
				params.setFilename(filename);
				params.setDelimiter(delimiter);
			}

			// Object req = Executions.getCurrent().getNativeRequest();
			// String reqstr="";
			// if(req !=null && req instanceof HttpServletRequest) {
			// HttpServletRequest servreq= (HttpServletRequest)req;
			// String forwardedfor= servreq.getHeader("x-forwarded-for");
			// if(forwardedfor!=null) reqstr=forwardedfor;
			// else reqstr= servreq.getRemoteAddr() + "-" + servreq.getRemoteHost();
			//
			// /*
			// String forwardedfor= servreq.getHeader("x-forwarded-for");
			// if(forwardedfor!=null) reqstr+="-" + forwardedfor;
			// */
			// }
			// String submitter=( (AppContext.isIRRILAN() ||
			// reqstr.contains(AppContext.getIRRIIp()))?reqstr+"-"+AppContext.createTempFilename():reqstr);

			params.setSubmitter("LOCAL_SCRIPT_SUBMITTER");

			/*
			 * params.setSubmitter( Sessions.getCurrent().getLocalAddr() +"-"+
			 * Sessions.getCurrent().getLocalName() + "-" +
			 * Sessions.getCurrent().getRemoteAddr() + "-" +
			 * Sessions.getCurrent().getRemoteHost() + "-" +
			 * Sessions.getCurrent().getServerName() + reqstr );
			 */

			// report = genotype.querydownloadGenotypeAsync(params);

			jobsfacade_orig = (JobsFacade) AppContext.checkBean(jobsfacade_orig, "JobsFacade");
			JobsFacade jobsfacade = jobsfacade_orig;

			jobsfacade.setLocalUser(true);

			if (params.getSubmitter() == null) {
				msg = "Submitter ID required for long jobs.";
			} else if (jobsfacade.checkSubmitter(params.getSubmitter())) {
				msg = "You have a running long job. Please try again when that job is done.";
			} else {
				AsyncJob job = new AsyncJobImpl(new File(params.getFilename()).getName(), params.toString(),
						params.getSubmitter());
				if (jobsfacade.addJob(job)) {
					Future futureReportjob = genotype.querydownloadGenotypeAsync(params);
					// AsyncJobReport rep=(AsyncJobReport)futureReportjob.get();
					// AppContext.debug( (rep==null? "rep=null":rep.getMessage() ));
					// rep.
					job.setFuture(futureReportjob);
					future = futureReportjob;
					msg = jobsfacade.JOBSTATUS_SUBMITTED;
					jobid = job.getJobId();
					url = job.getUrl();
					
					while(!futureReportjob.isDone())
						System.out.println("Still working on it");
				} else {
					msg = jobsfacade.JOBSTATUS_REFUSED;
				}
				
			
			}
			AppContext.debug("callableDownloadBigListbox.. submitted");
		} catch (Exception ex) {
			ex.printStackTrace();
			// Messagebox.show("call():" + ex.getMessage());
			msg = ex.getMessage();

		}
		
	}

	private List getGenotyperun() {
		List l = new ArrayList();
		Set intrunids = new HashSet<>();
		Iterator it = genotype.getGenotyperuns(dataset, snpset, "SNP").iterator();
		while (it.hasNext()) {
			GenotypeRunPlatform r = (GenotypeRunPlatform) it.next();
			if (intrunids.contains(r.getGenotypeRunId()) || intrunids.size() == 0)
				l.add(r);
		}
		return l;
	}

	
	


}
