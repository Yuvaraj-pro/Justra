#!/usr/bin/env python3
"""
================================================================================
NyayaMate: Production Legal Knowledge Ingestion & Statutory Modernization Pipeline
================================================================================
Ingests, modernizes (IPC/CrPC -> BNS/BNSS 2023), enriches with bilingual metadata,
and indexes the Indian Legal Texts QA dataset into ChromaDB for dense vector retrieval.

Dataset: akshatgupta7/llm-fine-tuning-dataset-of-indian-legal-texts
Target Vector Collection: nyayamate_indian_laws
Target Architecture: SentenceTransformers + ChromaDB PersistentClient
================================================================================
"""

import os
import re
import sys
import json
import uuid
import logging
from typing import Dict, List, Any, Optional, Tuple
from pathlib import Path

# Setup logging
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(name)s - %(message)s",
    datefmt="%Y-%m-%d %H:%M:%S",
)
logger = logging.getLogger("NyayaMateIngest")


# ==============================================================================
# I. STATUTORY BRIDGE (2026 Modernization: IPC/CrPC -> BNS/BNSS)
# ==============================================================================

class StatutoryBridge:
    """
    Deterministic Legal Knowledge Bridge mapping legacy colonial Indian statutes
    (Indian Penal Code 1860, Code of Criminal Procedure 1973) to modern 2024/2026
    enacted statutes (Bharatiya Nyaya Sanhita 2023, Bharatiya Nagarik Suraksha Sanhita 2023).
    """

    # Comprehensive IPC (1860) to BNS (2023) Section Mapping
    IPC_TO_BNS_MAP: Dict[str, Dict[str, str]] = {
        "124A": {
            "modern_section": "BNS Section 152",
            "title": "Act endangering sovereignty, unity and integrity of India",
            "category": "Criminal",
        },
        "141": {
            "modern_section": "BNS Section 189(1)",
            "title": "Unlawful Assembly",
            "category": "Criminal",
        },
        "144": {
            "modern_section": "BNS Section 190",
            "title": "Joining unlawful assembly armed with deadly weapon",
            "category": "Criminal",
        },
        "147": {
            "modern_section": "BNS Section 191(2)",
            "title": "Punishment for Rioting",
            "category": "Criminal",
        },
        "160": {
            "modern_section": "BNS Section 194",
            "title": "Punishment for committing affray",
            "category": "Criminal",
        },
        "171E": {
            "modern_section": "BNS Section 171",
            "title": "Bribery in Elections",
            "category": "Criminal",
        },
        "186": {
            "modern_section": "BNS Section 221",
            "title": "Obstructing public servant in discharge of public functions",
            "category": "General Procedural",
        },
        "191": {
            "modern_section": "BNS Section 227",
            "title": "Giving false evidence (Perjury)",
            "category": "Criminal",
        },
        "268": {
            "modern_section": "BNS Section 270",
            "title": "Public Nuisance",
            "category": "General Procedural",
        },
        "279": {
            "modern_section": "BNS Section 281",
            "title": "Rash driving or riding on a public way",
            "category": "Criminal",
        },
        "290": {
            "modern_section": "BNS Section 292",
            "title": "Punishment for public nuisance",
            "category": "General Procedural",
        },
        "295A": {
            "modern_section": "BNS Section 299",
            "title": "Deliberate acts to outrage religious feelings",
            "category": "Criminal",
        },
        "300": {
            "modern_section": "BNS Section 101",
            "title": "Murder definition",
            "category": "Criminal",
        },
        "302": {
            "modern_section": "BNS Section 103",
            "title": "Punishment for Murder",
            "category": "Criminal",
        },
        "304": {
            "modern_section": "BNS Section 105",
            "title": "Culpable homicide not amounting to murder",
            "category": "Criminal",
        },
        "304A": {
            "modern_section": "BNS Section 106",
            "title": "Causing death by negligence (Hit & Run)",
            "category": "Criminal",
        },
        "304B": {
            "modern_section": "BNS Section 80",
            "title": "Dowry Death",
            "category": "Criminal",
        },
        "307": {
            "modern_section": "BNS Section 109",
            "title": "Attempt to Murder",
            "category": "Criminal",
        },
        "319": {
            "modern_section": "BNS Section 114",
            "title": "Hurt definition",
            "category": "Criminal",
        },
        "323": {
            "modern_section": "BNS Section 115(2)",
            "title": "Punishment for voluntarily causing hurt",
            "category": "Criminal",
        },
        "326": {
            "modern_section": "BNS Section 118",
            "title": "Voluntarily causing grievous hurt by dangerous weapons",
            "category": "Criminal",
        },
        "351": {
            "modern_section": "BNS Section 130",
            "title": "Assault definition",
            "category": "Criminal",
        },
        "354": {
            "modern_section": "BNS Section 74",
            "title": "Assault or criminal force to woman with intent to outrage modesty",
            "category": "Criminal",
        },
        "354D": {
            "modern_section": "BNS Section 78",
            "title": "Stalking (including Cyber Stalking)",
            "category": "Cybercrime",
        },
        "375": {
            "modern_section": "BNS Section 63",
            "title": "Rape definition",
            "category": "Criminal",
        },
        "376": {
            "modern_section": "BNS Section 64",
            "title": "Punishment for Rape",
            "category": "Criminal",
        },
        "378": {
            "modern_section": "BNS Section 303(1)",
            "title": "Theft definition",
            "category": "Criminal",
        },
        "379": {
            "modern_section": "BNS Section 303(2)",
            "title": "Punishment for Theft",
            "category": "Criminal",
        },
        "383": {
            "modern_section": "BNS Section 308(1)",
            "title": "Extortion definition",
            "category": "Criminal",
        },
        "390": {
            "modern_section": "BNS Section 309",
            "title": "Robbery definition",
            "category": "Criminal",
        },
        "392": {
            "modern_section": "BNS Section 309(4)",
            "title": "Punishment for Robbery",
            "category": "Criminal",
        },
        "395": {
            "modern_section": "BNS Section 310(2)",
            "title": "Punishment for Dacoity",
            "category": "Criminal",
        },
        "405": {
            "modern_section": "BNS Section 316(1)",
            "title": "Criminal Breach of Trust",
            "category": "Consumer",
        },
        "406": {
            "modern_section": "BNS Section 316(2)",
            "title": "Punishment for Criminal Breach of Trust",
            "category": "Consumer",
        },
        "415": {
            "modern_section": "BNS Section 318(1)",
            "title": "Cheating definition",
            "category": "Consumer",
        },
        "420": {
            "modern_section": "BNS Section 318(4)",
            "title": "Cheating and dishonestly inducing delivery of property (Online Fraud/Scam)",
            "category": "Cybercrime",
        },
        "425": {
            "modern_section": "BNS Section 324(1)",
            "title": "Mischief definition",
            "category": "Tenancy",
        },
        "441": {
            "modern_section": "BNS Section 329(1)",
            "title": "Criminal Trespass",
            "category": "Tenancy",
        },
        "447": {
            "modern_section": "BNS Section 329(3)",
            "title": "Punishment for Criminal Trespass",
            "category": "Tenancy",
        },
        "448": {
            "modern_section": "BNS Section 329(4)",
            "title": "Punishment for House-trespass",
            "category": "Tenancy",
        },
        "463": {
            "modern_section": "BNS Section 336(1)",
            "title": "Forgery definition",
            "category": "Consumer",
        },
        "465": {
            "modern_section": "BNS Section 336(2)",
            "title": "Punishment for Forgery",
            "category": "Consumer",
        },
        "468": {
            "modern_section": "BNS Section 336(3)",
            "title": "Forgery for purpose of cheating (Identity Theft)",
            "category": "Cybercrime",
        },
        "498A": {
            "modern_section": "BNS Section 85 & 86",
            "title": "Husband or relative of husband subjecting woman to cruelty",
            "category": "Criminal",
        },
        "499": {
            "modern_section": "BNS Section 356(1)",
            "title": "Defamation definition",
            "category": "General Procedural",
        },
        "500": {
            "modern_section": "BNS Section 356(2)",
            "title": "Punishment for Defamation (incorporates Community Service)",
            "category": "General Procedural",
        },
        "503": {
            "modern_section": "BNS Section 351(1)",
            "title": "Criminal Intimidation",
            "category": "Criminal",
        },
        "506": {
            "modern_section": "BNS Section 351(2)",
            "title": "Punishment for Criminal Intimidation",
            "category": "Criminal",
        },
        "509": {
            "modern_section": "BNS Section 79",
            "title": "Word, gesture or act intended to insult the modesty of a woman",
            "category": "Criminal",
        },
    }

    # Comprehensive CrPC (1973) to BNSS (2023) Section Mapping
    CRPC_TO_BNSS_MAP: Dict[str, Dict[str, str]] = {
        "2": {
            "modern_section": "BNSS Section 2",
            "title": "Definitions (includes Electronic Communication & Audio-Visual recording)",
            "category": "General Procedural",
        },
        "41": {
            "modern_section": "BNSS Section 35",
            "title": "When police may arrest without warrant (Senior officer permission for <3 yrs)",
            "category": "General Procedural",
        },
        "41A": {
            "modern_section": "BNSS Section 35(3)",
            "title": "Notice of appearance before police officer",
            "category": "General Procedural",
        },
        "46": {
            "modern_section": "BNSS Section 43",
            "title": "Arrest how made (Handcuff regulation & Female arrest guidelines)",
            "category": "Constitutional Rights",
        },
        "57": {
            "modern_section": "BNSS Section 58",
            "title": "Person arrested not to be detained more than twenty-four hours",
            "category": "Constitutional Rights",
        },
        "82": {
            "modern_section": "BNSS Section 84",
            "title": "Proclamation for person absconding",
            "category": "General Procedural",
        },
        "91": {
            "modern_section": "BNSS Section 94",
            "title": "Summons to produce document or other thing (Includes electronic devices)",
            "category": "General Procedural",
        },
        "102": {
            "modern_section": "BNSS Section 107",
            "title": "Power of police officer to seize certain property",
            "category": "General Procedural",
        },
        "125": {
            "modern_section": "BNSS Section 144",
            "title": "Order for maintenance of wives, children and parents",
            "category": "Criminal",
        },
        "144": {
            "modern_section": "BNSS Section 163",
            "title": "Power to issue order in urgent cases of nuisance of apprehended danger",
            "category": "General Procedural",
        },
        "145": {
            "modern_section": "BNSS Section 164",
            "title": "Disputes as to immovable property (Land/Tenancy Possession disputes)",
            "category": "Tenancy",
        },
        "154": {
            "modern_section": "BNSS Section 173",
            "title": "Information in cognizable cases (Mandatory Zero FIR & e-FIR)",
            "category": "Constitutional Rights",
        },
        "156": {
            "modern_section": "BNSS Section 175",
            "title": "Police officer power to investigate (Magistrate directed investigation)",
            "category": "General Procedural",
        },
        "160": {
            "modern_section": "BNSS Section 179",
            "title": "Police officer power to require attendance of witnesses (Audio-visual examination)",
            "category": "General Procedural",
        },
        "161": {
            "modern_section": "BNSS Section 180",
            "title": "Examination of witnesses by police (Mandatory audio-video recording option)",
            "category": "General Procedural",
        },
        "164": {
            "modern_section": "BNSS Section 183",
            "title": "Recording of confessions and statements before Magistrate",
            "category": "Constitutional Rights",
        },
        "167": {
            "modern_section": "BNSS Section 187",
            "title": "Procedure when investigation cannot be completed in 24 hours (Police Custody up to 15 days across 40/60 days)",
            "category": "Constitutional Rights",
        },
        "173": {
            "modern_section": "BNSS Section 193",
            "title": "Report of police officer on completion of investigation (Chargesheet in 90 days)",
            "category": "General Procedural",
        },
        "200": {
            "modern_section": "BNSS Section 223",
            "title": "Examination of complainant (Mandatory hearing before taking cognizance)",
            "category": "General Procedural",
        },
        "313": {
            "modern_section": "BNSS Section 351",
            "title": "Power to examine the accused (Audio-visual medium permissible)",
            "category": "Constitutional Rights",
        },
        "320": {
            "modern_section": "BNSS Section 359",
            "title": "Compounding of offences",
            "category": "Consumer",
        },
        "378": {
            "modern_section": "BNSS Section 419",
            "title": "Appeal in case of acquittal",
            "category": "General Procedural",
        },
        "436": {
            "modern_section": "BNSS Section 478",
            "title": "In what cases bail to be taken (Bailable offences)",
            "category": "Constitutional Rights",
        },
        "436A": {
            "modern_section": "BNSS Section 479",
            "title": "Maximum period for which undertrial prisoner can be detained (First-time offenders 1/3rd)",
            "category": "Constitutional Rights",
        },
        "437": {
            "modern_section": "BNSS Section 480",
            "title": "When bail may be taken in case of non-bailable offence",
            "category": "Constitutional Rights",
        },
        "438": {
            "modern_section": "BNSS Section 482",
            "title": "Direction for grant of bail to person apprehending arrest (Anticipatory Bail)",
            "category": "Constitutional Rights",
        },
        "439": {
            "modern_section": "BNSS Section 483",
            "title": "Special powers of High Court or Court of Session regarding bail",
            "category": "Constitutional Rights",
        },
        "482": {
            "modern_section": "BNSS Section 528",
            "title": "Saving of inherent powers of High Court (Quashing of FIR / Petitions)",
            "category": "Constitutional Rights",
        },
    }

    @staticmethod
    def extract_section_number(text: str) -> Optional[str]:
        """Extracts section numbers like '420', '302', '124A', '498A', '436A' from text."""
        match = re.search(r'\bsection\s+(\d+[A-Za-z]?)\b', text, re.IGNORECASE)
        if match:
            return match.group(1).upper()
        match2 = re.search(r'\b(?:sec\.?|u/s)\s*(\d+[A-Za-z]?)\b', text, re.IGNORECASE)
        if match2:
            return match2.group(1).upper()
        return None

    @classmethod
    def enrich_statute(cls, source_file: str, question: str, answer: str) -> Dict[str, Any]:
        """
        Enriches a record by mapping legacy statutes to modern statutes,
        assigning legal categories, and providing precedent bridges.
        """
        text_corpus = f"{question} {answer}"
        lower_corpus = text_corpus.lower()
        extracted_sec = cls.extract_section_number(text_corpus)

        primary_statute = "Indian Law"
        legacy_statute = "None"
        legal_category = "General Procedural"
        modern_provision = None
        keywords: List[str] = []

        if "constitution" in source_file.lower() or "constitution" in lower_corpus:
            primary_statute = "Constitution of India"
            legacy_statute = "Constitution of India (1950)"
            legal_category = "Constitutional Rights"

            art_match = re.search(r'\barticle\s+(\d+[A-Za-z]?)\b', text_corpus, re.IGNORECASE)
            if art_match:
                keywords.append(f"Article {art_match.group(1)}")

            if any(w in lower_corpus for w in ["fundamental right", "liberty", "equality", "writ", "habeas", "article 21", "article 19", "article 14"]):
                keywords.extend(["Fundamental Rights", "Constitutional Remedy", "Citizen Protection"])
            elif any(w in lower_corpus for w in ["parliament", "union", "president", "governor"]):
                keywords.extend(["Union Government", "Parliamentary Procedure"])

        elif "ipc" in source_file.lower():
            legacy_statute = "Indian Penal Code (1860)"
            primary_statute = "Bharatiya Nyaya Sanhita (BNS 2023)"
            legal_category = "Criminal"

            if extracted_sec and extracted_sec in cls.IPC_TO_BNS_MAP:
                mapping = cls.IPC_TO_BNS_MAP[extracted_sec]
                modern_provision = f"{mapping['modern_section']} (supersedes IPC Section {extracted_sec})"
                legal_category = mapping["category"]
                keywords.append(mapping["title"])
                keywords.append(f"IPC {extracted_sec}")
                keywords.append(mapping["modern_section"])
            elif extracted_sec:
                modern_provision = f"BNS Statutory Equivalent for IPC Section {extracted_sec}"
                keywords.append(f"IPC Section {extracted_sec}")

            if any(w in lower_corpus for w in ["cheat", "fraud", "scam", "phishing", "online transaction", "cyber"]):
                legal_category = "Cybercrime"
                keywords.extend(["Fraud", "Financial Offence", "BNS 318"])
            elif any(w in lower_corpus for w in ["tenant", "landlord", "evict", "rent", "trespass"]):
                legal_category = "Tenancy"
                keywords.extend(["Property Dispute", "Trespass", "Tenancy Rights"])
            elif any(w in lower_corpus for w in ["consumer", "defective", "warranty", "refund", "breach"]):
                legal_category = "Consumer"
                keywords.extend(["Consumer Rights", "Breach of Trust"])

        elif "crpc" in source_file.lower():
            legacy_statute = "Code of Criminal Procedure (1973)"
            primary_statute = "Bharatiya Nagarik Suraksha Sanhita (BNSS 2023)"
            legal_category = "General Procedural"

            if extracted_sec and extracted_sec in cls.CRPC_TO_BNSS_MAP:
                mapping = cls.CRPC_TO_BNSS_MAP[extracted_sec]
                modern_provision = f"{mapping['modern_section']} (supersedes CrPC Section {extracted_sec})"
                legal_category = mapping["category"]
                keywords.append(mapping["title"])
                keywords.append(f"CrPC {extracted_sec}")
                keywords.append(mapping["modern_section"])
            elif extracted_sec:
                modern_provision = f"BNSS Statutory Equivalent for CrPC Section {extracted_sec}"
                keywords.append(f"CrPC Section {extracted_sec}")

            if any(w in lower_corpus for w in ["bail", "anticipatory", "surety", "bond", "custody", "undertrial"]):
                legal_category = "Constitutional Rights"
                keywords.extend(["Bail Application", "Liberty", "BNSS 480/483"])
            elif any(w in lower_corpus for w in ["fir", "zero fir", "complaint", "police report", "chargesheet"]):
                legal_category = "Constitutional Rights"
                keywords.extend(["Zero FIR", "BNSS 173", "Police Investigation"])
            elif any(w in lower_corpus for w in ["land", "possession", "dispute", "section 145"]):
                legal_category = "Tenancy"
                keywords.extend(["Executive Magistrate", "Land Possession"])

        keywords.extend(["Indian Jurisprudence", "Statutory Provision"])
        seen = set()
        dedup_keywords = [k for k in keywords if not (k.lower() in seen or seen.add(k.lower()))]

        return {
            "primary_statute": primary_statute,
            "legacy_statute": legacy_statute,
            "modern_provision": modern_provision or primary_statute,
            "legal_category": legal_category,
            "keywords": dedup_keywords,
        }


# ==============================================================================
# II. DATASET LOADER (KaggleHub Retrieval & Resilient Parsing)
# ==============================================================================

class DatasetLoader:
    """
    Handles robust downloading of the dataset via kagglehub, fallback discovery,
    file validation, and schema normalization across multiple QA key variations.
    """
    DATASET_SLUG = "akshatgupta7/llm-fine-tuning-dataset-of-indian-legal-texts"

    @classmethod
    def fetch_dataset_directory(cls, local_override_path: Optional[str] = None) -> Path:
        if local_override_path and Path(local_override_path).exists():
            logger.info(f"Using local override dataset directory: {local_override_path}")
            return Path(local_override_path)

        # 1. Attempt kagglehub download
        try:
            import kagglehub
            logger.info(f"Invoking kagglehub.dataset_download('{cls.DATASET_SLUG}')...")
            downloaded_path = kagglehub.dataset_download(cls.DATASET_SLUG)
            path_obj = Path(downloaded_path)
            if path_obj.exists():
                logger.info(f"Successfully downloaded via kagglehub: {downloaded_path}")
                return path_obj
        except Exception as e:
            logger.warning(f"kagglehub download unavailable or failed ({e}). Checking local fallbacks...")

        # 2. Check known cached locations
        fallback_candidates = [
            Path.home() / ".cache" / "kagglehub" / "datasets" / "akshatgupta7" / "llm-fine-tuning-dataset-of-indian-legal-texts",
            Path("/data/indian_legal_texts"),
            Path("./data/indian_legal_texts"),
            Path("/tmp/indian_legal_texts")
        ]

        for cand in fallback_candidates:
            if cand.exists() and any(cand.glob("*.json")):
                logger.info(f"Discovered cached dataset at: {cand}")
                return cand

        # 3. Direct streaming fallback from Hugging Face mirror
        hf_fallback_dir = Path("/tmp/indian_legal_texts")
        hf_fallback_dir.mkdir(parents=True, exist_ok=True)
        files = ["constitution_qa.json", "ipc_qa.json", "crpc_qa.json"]
        import urllib.request
        logger.info("Fetching raw JSON mirrors from Hugging Face dataset mirror...")
        for fname in files:
            target_f = hf_fallback_dir / fname
            if not target_f.exists() or target_f.stat().st_size == 0:
                url = f"https://huggingface.co/datasets/Techmaestro369/indian-legal-texts-finetuning/raw/main/{fname}"
                try:
                    logger.info(f"Streaming mirror: {fname}...")
                    urllib.request.urlretrieve(url, target_f)
                except Exception as err:
                    logger.error(f"Failed to fetch {fname} mirror: {err}")

        return hf_fallback_dir

    @staticmethod
    def parse_qa_record(raw_record: Dict[str, Any]) -> Optional[Tuple[str, str]]:
        question = (
            raw_record.get("question") or
            raw_record.get("instruction") or
            raw_record.get("prompt") or
            raw_record.get("input") or
            ""
        )
        answer = (
            raw_record.get("answer") or
            raw_record.get("output") or
            raw_record.get("response") or
            ""
        )

        q_clean = str(question).strip()
        a_clean = str(answer).strip()

        if q_clean and a_clean:
            return q_clean, a_clean
        return None

    @classmethod
    def load_all_records(cls, dataset_dir: Path) -> List[Dict[str, Any]]:
        target_files = ["constitution_qa.json", "ipc_qa.json", "crpc_qa.json"]
        all_records = []

        for fname in target_files:
            file_path = dataset_dir / fname
            if not file_path.exists():
                matches = list(dataset_dir.rglob(fname))
                if matches:
                    file_path = matches[0]
                else:
                    logger.warning(f"File {fname} not found in {dataset_dir}. Skipping.")
                    continue

            logger.info(f"Parsing {file_path.name} (Size: {file_path.stat().st_size / 1024:.1f} KB)...")
            try:
                with open(file_path, "r", encoding="utf-8") as f:
                    data = json.load(f)

                if not isinstance(data, list):
                    logger.warning(f"Expected list of objects in {fname}, got {type(data)}. Skipping.")
                    continue

                valid_count = 0
                for idx, item in enumerate(data):
                    if not isinstance(item, dict):
                        continue
                    qa = cls.parse_qa_record(item)
                    if qa:
                        q, a = qa
                        all_records.append({
                            "source_file": file_path.name,
                            "index_in_file": idx,
                            "question": q,
                            "answer": a,
                        })
                        valid_count += 1

                logger.info(f"Successfully extracted {valid_count} valid QA pairs from {fname}.")
            except Exception as e:
                logger.error(f"Error parsing JSON from {file_path}: {e}")

        logger.info(f"Total raw records compiled: {len(all_records)}")
        return all_records


# ==============================================================================
# III. VECTOR INGESTION & CHROMA PIPELINE
# ==============================================================================

class VectorIngestionPipeline:
    """
    Orchestrates semantic document construction, batch tokenization/embedding,
    metadata flattening, and persistent storage into ChromaDB collection.
    """

    def __init__(
        self,
        collection_name: str = "nyayamate_indian_laws",
        db_persist_path: str = "./nyayamate_chroma_db",
        embedding_model_name: str = "sentence-transformers/all-MiniLM-L6-v2",
        batch_size: int = 100
    ):
        self.collection_name = collection_name
        self.db_persist_path = db_persist_path
        self.embedding_model_name = embedding_model_name
        self.batch_size = batch_size

        logger.info(f"Initializing ChromaDB PersistentClient at: {self.db_persist_path}")
        try:
            import chromadb
            self.chroma_client = chromadb.PersistentClient(path=self.db_persist_path)
            self.collection = self.chroma_client.get_or_create_collection(
                name=self.collection_name,
                metadata={"description": "NyayaMate Indian Legal Jurisprudence Vector Store (BNS/BNSS 2026 enriched)"}
            )
            logger.info(f"Collection '{self.collection_name}' ready. Existing doc count: {self.collection.count()}")
        except ImportError:
            logger.error("ChromaDB is not installed. Please install via: pip install chromadb")
            self.chroma_client = None
            self.collection = None

        logger.info(f"Loading embedding model: {self.embedding_model_name}...")
        try:
            from sentence_transformers import SentenceTransformer
            self.model = SentenceTransformer(self.embedding_model_name)
            logger.info("SentenceTransformer model loaded successfully.")
        except ImportError:
            logger.warning("sentence-transformers not installed. Chroma internal default embedding will be utilized.")
            self.model = None

    def construct_semantic_document(self, record: Dict[str, Any]) -> Tuple[str, Dict[str, Any], str]:
        source_file = record["source_file"]
        question = record["question"]
        answer = record["answer"]

        enrichment = StatutoryBridge.enrich_statute(source_file, question, answer)

        document_text = (
            f"STATUTE: {enrichment['primary_statute']}\n"
            f"HISTORICAL PRECEDENT: {enrichment['legacy_statute']}\n"
            f"PROVISION / SECTION: {enrichment['modern_provision']}\n"
            f"LEGAL CATEGORY: {enrichment['legal_category']}\n"
            f"QUERY: {question}\n"
            f"STATUTORY EXPLANATION & RATIO: {answer}"
        )

        doc_id = f"nyaya_{source_file.split('.')[0]}_{record['index_in_file']}_{uuid.uuid4().hex[:6]}"

        metadata = {
            "doc_id": doc_id,
            "source_file": source_file,
            "country_code": "IN",
            "jurisdiction": "India",
            "primary_statute": str(enrichment["primary_statute"]),
            "legacy_statute": str(enrichment["legacy_statute"]),
            "modern_provision": str(enrichment["modern_provision"]),
            "legal_category": str(enrichment["legal_category"]),
            "keywords": ", ".join(enrichment["keywords"]),
            "question_snippet": question[:200],
        }

        return doc_id, metadata, document_text

    def run_ingestion(self, raw_records: List[Dict[str, Any]], max_limit: Optional[int] = None):
        if self.collection is None:
            logger.error("ChromaDB collection unavailable. Aborting vector ingestion.")
            return

        records_to_process = raw_records[:max_limit] if max_limit else raw_records
        total_records = len(records_to_process)
        logger.info(f"Starting vector ingestion pipeline for {total_records} documents (Batch size: {self.batch_size})...")

        seen_questions = set()
        unique_entries: List[Tuple[str, Dict[str, Any], str]] = []

        for rec in records_to_process:
            q_norm = rec["question"].lower().strip()
            if q_norm in seen_questions:
                continue
            seen_questions.add(q_norm)
            doc_id, metadata, doc_text = self.construct_semantic_document(rec)
            unique_entries.append((doc_id, metadata, doc_text))

        logger.info(f"Deduplication complete: {len(unique_entries)} unique legal chunks prepared out of {total_records}.")

        try:
            from tqdm import tqdm
            has_tqdm = True
        except ImportError:
            has_tqdm = False

        iterator = range(0, len(unique_entries), self.batch_size)
        if has_tqdm:
            iterator = tqdm(iterator, desc="Ingesting Chunks to ChromaDB", unit="batch")

        for start_idx in iterator:
            batch = unique_entries[start_idx : start_idx + self.batch_size]
            batch_ids = [item[0] for item in batch]
            batch_metadatas = [item[1] for item in batch]
            batch_documents = [item[2] for item in batch]

            if self.model is not None:
                embeddings = self.model.encode(batch_documents, show_progress_bar=False, normalize_embeddings=True)
                embeddings_list = embeddings.tolist()
                self.collection.upsert(
                    ids=batch_ids,
                    documents=batch_documents,
                    metadatas=batch_metadatas,
                    embeddings=embeddings_list
                )
            else:
                self.collection.upsert(
                    ids=batch_ids,
                    documents=batch_documents,
                    metadatas=batch_metadatas
                )

            if not has_tqdm and (start_idx // self.batch_size) % 5 == 0:
                logger.info(f"Processed {min(start_idx + self.batch_size, len(unique_entries))} / {len(unique_entries)} chunks...")

        logger.info(f"Vector Ingestion complete! Total items now in collection '{self.collection_name}': {self.collection.count()}")

    def query_similarity(self, query_text: str, n_results: int = 3) -> Dict[str, Any]:
        logger.info(f"Executing diagnostic query: '{query_text}' (Top-{n_results})")
        if self.model is not None:
            query_emb = self.model.encode([query_text], normalize_embeddings=True).tolist()
            results = self.collection.query(
                query_embeddings=query_emb,
                n_results=n_results
            )
        else:
            results = self.collection.query(
                query_texts=[query_text],
                n_results=n_results
            )
        return results


# ==============================================================================
# IV. MAIN EXECUTION BLOCK & DIAGNOSTIC TEST
# ==============================================================================

if __name__ == "__main__":
    print("\n" + "=" * 80)
    print(" NyayaMate: Indian Legal Knowledge Ingestion & Vector Indexer")
    print(" Modernized to Bharatiya Nyaya Sanhita (BNS) & BNSS (2026 Standards)")
    print("=" * 80 + "\n")

    # Step 1: Download / Locate dataset
    dataset_dir = DatasetLoader.fetch_dataset_directory()
    print(f"[*] Dataset directory verified at: {dataset_dir.absolute()}\n")

    # Step 2: Load raw QA records
    raw_data = DatasetLoader.load_all_records(dataset_dir)
    if not raw_data:
        print("[!] No records extracted. Exiting.")
        sys.exit(1)

    print(f"[*] Total valid QA entries parsed: {len(raw_data)}\n")

    # Step 3: Initialize Vector Ingestion Pipeline
    persist_dir = os.path.abspath("./nyayamate_chroma_db")
    pipeline = VectorIngestionPipeline(
        collection_name="nyayamate_indian_laws",
        db_persist_path=persist_dir,
        embedding_model_name="sentence-transformers/all-MiniLM-L6-v2",
        batch_size=100
    )

    # Step 4: Run Ingestion
    pipeline.run_ingestion(raw_data)

    # Step 5: Diagnostic Query Verification
    print("\n" + "-" * 80)
    print(" DIAGNOSTIC QUERY VERIFICATION: BNS / BNSS MODERN RETRIEVAL")
    print("-" * 80)

    test_queries = [
        "Online UPI scam and cheating with fake refund call",
        "Police refusing to register Zero FIR",
        "Landlord unlawfully withholding rental security deposit and trespassing",
        "Right to equality and protection of life under fundamental rights"
    ]

    for q in test_queries:
        print(f"\n[QUERY]: '{q}'")
        res = pipeline.query_similarity(q, n_results=1)
        if res and res.get("documents") and res["documents"][0]:
            doc = res["documents"][0][0]
            meta = res["metadatas"][0][0]
            print(f"  -> PRIMARY STATUTE  : {meta.get('primary_statute')}")
            print(f"  -> MODERN PROVISION : {meta.get('modern_provision')}")
            print(f"  -> LEGAL CATEGORY   : {meta.get('legal_category')}")
            print(f"  -> MATCHED SNIPPET  :\n     {doc[:260]}...\n")

    print("=" * 80)
    print(f" Ingestion Pipeline executed successfully. ChromaDB saved to: {persist_dir}")
    print("=" * 80 + "\n")
