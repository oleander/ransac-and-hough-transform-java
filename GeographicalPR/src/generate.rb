OFFSET_PERCENT = 1
def mess(x)
   x + (x * rand(OFFSET_PERCENT) / 100.0) * [-1, 1].sample
end

def c(a,b,r); tt = t; [(a + r * Math.cos(tt)).round.to_i, (b + r * Math.sin(tt)).round.to_i]; end
def t; rand(100) / 100.0 * 2 * Math::PI; end

m = 100

raw = m.times.map do 
  x, y = c(0, 0, 50)
  [mess(x).round.to_i, mess(y).round.to_i].join(",") 
end

raw += m.times.map do 
  x, y = c(-80, -100, 50)
  [mess(x).round.to_i, mess(y).round.to_i].join(",") 
end

raw += m.times.map do 
  x, y = c(80, 120, 90)
  [mess(x).round.to_i, mess(y).round.to_i].join(",") 
end

# raw += (2 * m).times.map do 
#   x, y = c(1, -130, 100)
#   [mess(x).round.to_i, mess(y).round.to_i].join(",") 
# end

raw += 50.times.map do 
  [rand(200) * [-1, 1].sample, rand(200) * [-1, 1].sample].join(",") 
end

# raw += 150.times.map do
#   [rand(800).round.to_i * [-1, 1].sample, rand(800).round.to_i * [-1, 1].sample , "false"].join(",") 
# end

File.open("pc.data", "w") { |file| file.write(raw.shuffle.join("\n")) }
