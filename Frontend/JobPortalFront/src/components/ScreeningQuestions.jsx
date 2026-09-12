import { useState, useEffect, useRef } from "react";

export default function ScreeningQuestions({ questions = [], onNext }) {
  const [answers, setAnswers] = useState([]);
  const inputRefs = useRef([]);

  useEffect(() => {
    setAnswers(questions.map(() => ""));
    // Autofocus first input
    if (questions.length > 0) {
      setTimeout(() => {
        inputRefs.current[0]?.focus();
      }, 100);
    }
  }, [questions]);

  const handleChange = (index, value) => {
    const updated = [...answers];
    updated[index] = value;
    setAnswers(updated);
  };

  const allAnswered = answers.every((answer) => answer.trim() !== "");

  return (
    <div className="space-y-6 animate-fade-in">
      <h2 className="text-2xl font-semibold text-gray-900 mb-4">
        Let’s get to know you first
      </h2>

      {questions.map((question, index) => (
        <div key={index} className="space-y-2">
          <label
            htmlFor={`question-${index}`}
            className="block text-md font-medium text-gray-800"
          >
            {question}
          </label>
          <input
            id={`question-${index}`}
            type="text"
            ref={(el) => (inputRefs.current[index] = el)}
            className="w-full border border-gray-300 focus:border-[#6B3F27] focus:ring focus:ring-[#6B3F27]/20 px-3 py-2.5 text-base rounded-md transition-all outline-none"
            placeholder="Type your answer here..."
            value={answers[index] || ""}
            onChange={(e) => handleChange(index, e.target.value)}
            required
          />
        </div>
      ))}

      <button
        type="button"
        onClick={() => onNext(answers)}
        disabled={!allAnswered}
        className={`w-full sm:w-fit px-6 py-2.5 min-h-[44px] rounded-md text-white font-medium transition-all ${
          allAnswered
            ? "bg-[#6B3F27] hover:bg-[#5C3421]"
            : "bg-gray-400 cursor-not-allowed"
        }`}
      >
        Next
      </button>
    </div>
  );
}
