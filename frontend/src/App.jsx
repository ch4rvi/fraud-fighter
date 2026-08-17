import { useState } from 'react'
import './App.css'

function App() {
  const [debtorAccountNumber, setDebtorAccountNumber] = useState("123")
  const [debtorBankCode, setDebtorBankCode] = useState("100")
  const [creditorAccountNumber, setCreditorAccountNumber] = useState("234")
  const [creditorBankCode, setCreditorBankCode] = useState("100")
  const [currency, setCurrency] = useState("CZK")
  const [amount, setAmount] = useState("1")
  const [decisionResponse, setDecisionResponse] = useState("")
  const [triggeredRule, setTriggeredRule] = useState("")

  const sendTransactionRequest = {
    "debtorAccount": {
      "accountNumber": debtorAccountNumber,
      "bankCode": debtorBankCode,
    },
    "creditorAccount": {
      "accountNumber": creditorAccountNumber,
      "bankCode": creditorBankCode,
    },
    "currency": currency,
    "amount": amount,
    "source": "IB"
  }

  const sendTransaction = async () => {
    let response = await fetch(
        "http://localhost:8080/api/fraud/transaction",
        {
          "method": "POST",
          "headers": {"Content-Type": "application/json"},
          "body": JSON.stringify(sendTransactionRequest)
        }
    );
    console.log(sendTransactionRequest);
    let sendTransactionResponse = await response.json();
    if (sendTransactionResponse == null) {
      console.log("Decision is probably empty!");
    } else {
      console.log(sendTransactionResponse)
      console.log(setDecisionResponse(sendTransactionResponse.decisionAction));
      console.log(setTriggeredRule(sendTransactionResponse && sendTransactionResponse.triggeredRules[0] ? sendTransactionResponse.triggeredRules[0] : ""));
      sendTransactionResponse = null;
    }
  }

  return (
    <>
      <section id="center">
        <div>
          <h1>SEND TRN</h1>
          <p>
            Fill in the form to send transaction.
          </p>
        </div>
        <input name="debtorAccountNumber"
               type="text"
               value={debtorAccountNumber}
               onChange={(event) => setDebtorAccountNumber(event.target.value)}></input>
        <input name="debtorBankCode"
               type="text"
               value={debtorBankCode}
               onChange={(event) => setDebtorBankCode(event.target.value)}></input>
        <input name="creditorAccountNumber"
               type="text"
               value={creditorAccountNumber}
               onChange={(event) => setCreditorAccountNumber(event.target.value)}></input>
        <input name="creditorBankCode"
               type="text"
               value={creditorBankCode}
               onChange={(event) => setCreditorBankCode(event.target.value)}></input>
        <input name="currency"
               type="text"
               value={currency}
               onChange={(event) => setCurrency(event.target.value)}></input>
        <input name="amount"
               type="text"
               value={amount}
               onChange={(event)=> setAmount(event.target.value)}></input>
        <input name="submit" type="submit"
               onClick={sendTransaction}></input>


        <p>Vepsaná hodnota je {debtorAccountNumber}</p>
        <p>Vepsaná hodnota je {debtorBankCode}</p>
        <p>Vepsaná hodnota je {creditorAccountNumber}</p>
        <p>Vepsaná hodnota je {creditorBankCode}</p>
        <p>Vepsaná hodnota je {currency}</p>
        <p>Vepsaná hodnota je {amount}</p>

        <h3>Výsledek je {decisionResponse}</h3>
        <h3>Triggované pravidlo je {triggeredRule}</h3>

      </section>
    </>
  )
}

export default App
